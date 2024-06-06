package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EmailChangeTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.MailBody;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.util.Authority.Code;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.EmailChangeTokenDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.UserDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.security.SecurityUtil;
import at.ac.tuwien.sepr.groupphase.backend.service.AddressService;
import at.ac.tuwien.sepr.groupphase.backend.service.EmailSenderService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.MailNotSentException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.UserValidator;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserDao userDao;
    private final EmailChangeTokenDao emailChangeTokenDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final UserValidator userValidator;
    private final EmailSenderService emailSenderService;
    private final AddressService addressService;

    @Autowired
    public UserServiceImpl(UserDao userDao, EmailChangeTokenDao emailChangeTokenDao, PasswordEncoder passwordEncoder,
                           JwtTokenizer jwtTokenizer, UserValidator userValidator,
                           EmailSenderService emailSenderService, AddressService addressService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.userValidator = userValidator;
        this.addressService = addressService;
        this.emailSenderService = emailSenderService;
        this.emailChangeTokenDao = emailChangeTokenDao;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        ApplicationUserDto applicationUser = findApplicationUserByEmail(email);
        return getUserDetailsForUser(applicationUser);
    }

    private UserDetails getUserDetailsForUser(ApplicationUserDto applicationUser) {
        try {
            List<GrantedAuthority> grantedAuthorities;
            if (applicationUser.getAdmin()) {
                grantedAuthorities = AuthorityUtils.createAuthorityList(Code.ADMIN, Code.USER);
            } else {
                grantedAuthorities = AuthorityUtils.createAuthorityList(Code.USER);
            }

            return new User(applicationUser.getEmail(), applicationUser.getPassword(), grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUserDto findApplicationUserByEmail(String email) {
        LOGGER.debug("Find application user by email");
        ApplicationUserDto applicationUser = userDao.findByEmail(email);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        try {
            ApplicationUserDto applicationUserDto = findApplicationUserByEmail(userLoginDto.getEmail());
            UserDetails userDetails = getUserDetailsForUser(applicationUserDto);
            if (userDetails != null
                && userDetails.isAccountNonExpired()
                && userDetails.isAccountNonLocked()
                && userDetails.isCredentialsNonExpired()
                && passwordEncoder.matches(userLoginDto.getPassword().concat(applicationUserDto.getSalt()),
                userDetails.getPassword())
            ) {
                List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
                return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
            }
        } catch (NotFoundException e) {
            throw new BadCredentialsException("Username or password is incorrect or account is locked");
        }
        throw new BadCredentialsException("Username or password is incorrect or account is locked");
    }

    public ApplicationUserDto createUser(ApplicationUserDto toCreate) throws ValidationException, ForbiddenException {
        LOGGER.debug("Create user");
        userValidator.validateForCreate(toCreate);
        AddressDto addressDto = addressService.create(toCreate.getAddress());
        toCreate.setAddress(addressDto);
        toCreate.setSalt(SecurityUtil.generateSalt(32));
        toCreate.setPassword(passwordEncoder.encode(toCreate.getPassword() + toCreate.getSalt()));
        return userDao.create(toCreate);
    }

    @Override
    public Page<ApplicationUserDto> search(ApplicationUserSearchDto searchParameters) {
        LOGGER.debug("Search for users: {}", searchParameters);
        return userDao.search(searchParameters);
    }

    @Override
    public ApplicationUserDto updateUserStatusByEmail(ApplicationUserDto toUpdate, String adminEmail)
        throws NotFoundException, ValidationException {
        LOGGER.debug("Update user status: {}", toUpdate);
        userValidator.validateForUpdateStatus(toUpdate, adminEmail);
        ApplicationUserDto user = userDao.findByEmail(toUpdate.getEmail());
        if (user == null) {
            throw new NotFoundException("Could not update the user with the email address " + toUpdate.getEmail()
                + " because it does not exist");
        }
        if (user.getSuperAdmin()) {
            throw new ValidationException("Cannot update the status of this user.");
        }
        try {
            user.setAccountLocked(toUpdate.isAccountLocked());
            return userDao.update(user);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    @Override
    public ApplicationUserDto updateUserInfo(ApplicationUserDto userInfo) throws ValidationException,
        MailNotSentException {
        LOGGER.debug("Update user info: {}", userInfo);
        ApplicationUserDto user;

        try {
            user = userDao.findById(userInfo.getId());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Could not update the user because it does not exist.");
        }

        // Update phone number if it is not null or empty
        if (userInfo.getPhoneNumber() != null) {
            user.setPhoneNumber(userInfo.getPhoneNumber());
        }

        // Validate user information
        userValidator.validateForUpdate(user);

        // Update email if it is different from the current email
        if ((userInfo.getEmail() != null) && !Objects.equals(user.getEmail(),
            userInfo.getEmail())) {
            ApplicationUserDto userByEmail = null;
            try {
                userByEmail = findApplicationUserByEmail(userInfo.getEmail());
            } catch (NotFoundException e) {
                // Do nothing
            }
            if (userByEmail != null) {
                throw new ValidationException("The new email address is already in use.");
            }

            // Create a token for email change
            EmailChangeTokenDto emailChangeToken = createAndSaveEmailChangeToken(userInfo.getEmail(), user.getEmail());

            // Send email to the new email address to confirm the change
            MailBody mailBody = generateMailBodyChangeEmail(userInfo, user, emailChangeToken.getToken());
            try {
                emailSenderService.sendHtmlMail(mailBody);
            } catch (MessagingException e) {
                throw new MailNotSentException("Error sending mail.", e);
            }
        }

        // Update user information
        try {
            return userDao.update(user);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Could not update the user because it does not exist.");
        }
    }

    @Override
    public ApplicationUserDto findApplicationUserById(Long id) throws NotFoundException {
        if (id != null) {
            try {
                return userDao.findById(id);
            } catch (EntityNotFoundException e) {
                throw new NotFoundException("Could not find the user with the id " + id);
            }
        }
        return null;
    }

    @Override
    public ApplicationUserDto updateUserEmailWithValidToken(String token) {
        EmailChangeTokenDto emailChangeToken = emailChangeTokenDao.findByToken(token);
        if (emailChangeToken != null && emailChangeToken.getExpiryDate().isAfter(LocalDateTime.now())) {
            ApplicationUserDto user = userDao.findByEmail(emailChangeToken.getCurrentEmail());
            user.setEmail(emailChangeToken.getNewEmail());
            ApplicationUserDto updatedUser = null;
            try {
                updatedUser = userDao.update(user);
            } catch (EntityNotFoundException e) {
                LOGGER.error("Could not update the user with the email address {} because it does not exist.",
                    emailChangeToken.getCurrentEmail());
            }
            invalidateOldTokens(emailChangeToken.getCurrentEmail());
            return updatedUser;
        }
        return null;
    }

    private EmailChangeTokenDto createAndSaveEmailChangeToken(String newEmail, String currentEmail) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(10);

        EmailChangeTokenDto emailChangeToken = new EmailChangeTokenDto();
        emailChangeToken.setToken(token);
        emailChangeToken.setNewEmail(newEmail);
        emailChangeToken.setCurrentEmail(currentEmail);
        emailChangeToken.setExpiryDate(expiryDate);

        // Invalidate old tokens for the current email
        invalidateOldTokens(currentEmail);

        return emailChangeTokenDao.create(emailChangeToken);
    }

    private void invalidateOldTokens(String currentEmail) {
        emailChangeTokenDao.findByCurrentEmail(currentEmail).forEach(token -> {
            token.setExpiryDate(LocalDateTime.now().minusMinutes(1));
            try {
                emailChangeTokenDao.update(token);  // Save the updated token
            } catch (EntityNotFoundException e) {
                throw new NotFoundException("Could not update the email change token because it does not exist.");
            }
        });
    }

    private MailBody generateMailBodyChangeEmail(ApplicationUserDto userInfo, ApplicationUserDto user, String token) {
        String email = userInfo.getEmail();
        String subject = "Ihre E-Mail Adresse wurde geändert.";
        String url = "http://localhost:8080/api/v1/users/update/user/email?token=" + token;

        StringBuilder emailTemplate = new StringBuilder();
        emailTemplate.append("<!DOCTYPE html>\n")
            .append("<html lang=\"de\">\n")
            .append("<head>\n")
            .append("   <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n")
            .append("   <style>\n")
            .append("      body { background-color: #f8f9fa; font-family: Arial, sans-serif; }\n")
            .append("      .container { width: 100%; max-width: 600px; margin: 0 auto; padding: 20px; }\n")
            .append("      .card { background-color: #ffffff; border: 1px solid #dee2e6; border-radius: 0.25rem; padding: 20px; }\n")
            .append("      .card-body { padding: 20px; }\n")
            .append("      .h3 { font-size: 1.75rem; margin-bottom: 0.5rem; }\n")
            .append("      .h5 { font-size: 1.25rem; }\n")
            .append("      .text-gray-700 { color: #6c757d; }\n")
            .append("      .btn-primary { display: inline-block; font-weight: 400; color: #fff !important; text-align: center; vertical-align: middle; cursor: pointer; background-color: #007bff; border: 1px solid #007bff; padding: 0.375rem 0.75rem; font-size: 1rem; border-radius: 0.25rem; text-decoration: none; }\n")
            .append("   </style>\n")
            .append("</head>\n")
            .append("<body>\n")
            .append("    <div class=\"container\">\n")
            .append("      <div class=\"card my-10\">\n")
            .append("        <div class=\"card-body\">\n")
            .append("          <h1 class=\"h3\">Bestätigung E-Mail Änderung</h1>\n")
            .append("          <h5 class=\"h5\">Hallo ").append(user.getFirstName()).append("!</h5>\n")
            .append("          <hr>\n")
            .append("          <div>\n")
            .append("            <p class=\"text-gray-700\">Wir möchten bestätigen, dass du ")
            .append(userInfo.getEmail())
            .append(" als deine E-Mail für TicketLine bevorzugst.</p>\n")
            .append("            <p class=\"text-gray-700\">Falls du deine E-Mail-Adresse nicht ändern und weiterhin die ")
            .append("aktuelle E-Mail-Adresse ").append(user.getEmail())
            .append(" verwenden möchtest, ignoriere einfach diese E-Mail.</p>\n")
            .append("            <p class=\"text-gray-700\">Bis du diese Änderung bestätigst, musst du deine aktuelle ")
            .append("E-Mail-Adresse verwenden, um dich bei TicketLine anzumelden.</p>\n")
            .append("          <hr>\n")
            .append("          <a class=\"btn btn-primary\" href=\"").append(url)
            .append("\" target=\"_blank\" style=\"color: #fff !important;\">E-Mail Adresse bestätigen</a>\n")
            .append("            <p class=\"text-gray-700\">Dieser Link ist für die nächsten 10 Minuten gültig.</p>\n")
            .append("            <p class=\"text-gray-700\">Dies ist eine automatisch generierte E-Mail – bitte antworte nicht auf diese E-Mail.</p>\n")
            .append("          </div>\n")
            .append("        </div>\n")
            .append("      </div>\n")
            .append("    </div>\n")
            .append("  </body>\n")
            .append("</html>");

        return new MailBody(email, subject, emailTemplate.toString());
    }

}
