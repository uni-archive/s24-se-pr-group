package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EmailChangeTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.MailBody;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.mapper.EmailChangeTokenMapper;
import at.ac.tuwien.sepr.groupphase.backend.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.util.Authority.Code;
import at.ac.tuwien.sepr.groupphase.backend.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.EmailChangeTokenDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.UserDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.security.SecurityUtil;
import at.ac.tuwien.sepr.groupphase.backend.service.EmailSenderService;
import at.ac.tuwien.sepr.groupphase.backend.service.AddressService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.MailNotSentException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.UserValidator;
import jakarta.mail.MessagingException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserDao userDao;
    private final EmailChangeTokenDao emailChangeTokenDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final UserValidator userValidator;
    private final EmailSenderService emailSenderService;
    private final EmailChangeTokenMapper emailChangeTokenMapper;
    private final AddressService addressService;

    @Autowired
    public UserServiceImpl(UserDao userDao, EmailChangeTokenDao emailChangeTokenDao, PasswordEncoder passwordEncoder,
                           JwtTokenizer jwtTokenizer, UserValidator userValidator,
                           EmailChangeTokenMapper emailChangeTokenMapper, EmailSenderService emailSenderService, AddressService addressService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.userValidator = userValidator;
        this.addressService = addressService;
        this.emailSenderService = emailSenderService;
        this.emailChangeTokenDao = emailChangeTokenDao;
        this.emailChangeTokenMapper = emailChangeTokenMapper;
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
            if (applicationUser.isAdmin()) {
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
    public Stream<ApplicationUserDto> search(ApplicationUserSearchDto searchParameters) {
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
        if (userInfo.getPhoneNumber() != null || !userInfo.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(userInfo.getPhoneNumber());
        }

        // Validate user information
        userValidator.validateForUpdate(user);

        // Update email if it is different from the current email
        if ((userInfo.getEmail() != null || !userInfo.getEmail().isBlank()) && !Objects.equals(user.getEmail(),
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

        String emailTemplate = "<html>"
            + "<body>"
            + "<p>Hallo " + user.getFirstName() + ",</p>"
            + "<p>Wir möchten bestätigen, dass du " + userInfo.getEmail() + " als deine E-Mail für TicketLine"
            + " bevorzugst.</p>"
            + "<p>Falls du deine E-Mail-Adresse nicht ändern und weiterhin die aktuelle E-Mail-Adresse " + user.getEmail()
            + " verwenden möchtest, ignoriere einfach diese E-Mail.</p>"
            + "<p>Bis du diese Änderung bestätigst, musst du deine aktuelle E-Mail-Adresse verwenden, um "
            + "dich bei TicketLine anzumelden.</p>"
            + "<a href=\"" + url + "\" style=\"display: inline-block; padding: 10px 20px; "
            + "font-size: 16px; color: #fff; background-color: #007bff; text-decoration: none; border-radius:"
            + " 5px;\">E-Mail Adresse ändern</a>"
            + "<p>Dieser Link ist für die nächsten 10 Minuten gültig.</p>"
            + "<p>Dies ist eine automatisch generierte E-Mail – bitte antworten Sie nicht auf diese E-Mail"
            + ".</p>"
            + "</body>"
            + "</html>";
        return new MailBody(email, subject, emailTemplate);
    }
}
