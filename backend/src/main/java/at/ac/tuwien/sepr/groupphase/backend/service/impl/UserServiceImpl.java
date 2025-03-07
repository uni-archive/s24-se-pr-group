package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.AccountActivateTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EmailChangeTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.MailBody;
import at.ac.tuwien.sepr.groupphase.backend.dto.NewPasswordTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.util.Authority.Code;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.AccountActivateTokenDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.EmailChangeTokenDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.NewPasswordTokenDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.UserDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.security.SecurityUtil;
import at.ac.tuwien.sepr.groupphase.backend.service.AddressService;
import at.ac.tuwien.sepr.groupphase.backend.service.EmailSenderService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.MailNotSentException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.UserLockedException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.UserValidator;
import com.google.common.cache.Cache;
import jakarta.mail.MessagingException;
import org.quartz.SchedulerException;
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
import java.util.concurrent.ExecutionException;

@Service
public class UserServiceImpl implements UserService {

    public static final String ACCOUNT_LOCKED = "Ihr Account wurde wegen wiederholter falscher Anmeldeversuche gesperrt. Bitte versuchen Sie es später erneut oder kontaktieren Sie einen Administrator.";
    public static final String INCORRECT_USERNAME_OR_PASSWORD = "Nutzername oder Passwort sind falsch.";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int MAX_EXPIRATION_TIME = 5;
    private final UserDao userDao;
    private final EmailChangeTokenDao emailChangeTokenDao;
    private final NewPasswordTokenDao newPasswordTokenDao;
    private final AccountActivateTokenDao accountActivateTokenDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final UserValidator userValidator;
    private final EmailSenderService emailSenderService;
    private final AddressService addressService;
    private final Cache<String, Integer> loginAttemptCache;
    private final UserUnlockSchedulingService userUnlockSchedulingService;

    @Autowired
    public UserServiceImpl(UserDao userDao, EmailChangeTokenDao emailChangeTokenDao, PasswordEncoder passwordEncoder,
                           JwtTokenizer jwtTokenizer, UserValidator userValidator, AccountActivateTokenDao accountActivateTokenDao, EmailSenderService emailSenderService,
                           AddressService addressService, Cache<String, Integer> loginAttemptCache, NewPasswordTokenDao newPasswordTokenDao,
                           UserUnlockSchedulingService userUnlockSchedulingService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.userValidator = userValidator;
        this.addressService = addressService;
        this.emailSenderService = emailSenderService;
        this.emailChangeTokenDao = emailChangeTokenDao;
        this.loginAttemptCache = loginAttemptCache;
        this.newPasswordTokenDao = newPasswordTokenDao;
        this.userUnlockSchedulingService = userUnlockSchedulingService;
        this.accountActivateTokenDao = accountActivateTokenDao;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        ApplicationUserDto applicationUser = null;
        try {
            applicationUser = findApplicationUserByEmail(email);
        } catch (DtoNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
        return getUserDetailsForUser(applicationUser);
    }

    private UserDetails getUserDetailsForUser(ApplicationUserDto applicationUser) {
        List<GrantedAuthority> grantedAuthorities;
        if (applicationUser.getAdmin()) {
            grantedAuthorities = AuthorityUtils.createAuthorityList(Code.ADMIN, Code.USER);
        } else {
            grantedAuthorities = AuthorityUtils.createAuthorityList(Code.USER);
        }

        return new User(applicationUser.getEmail(), applicationUser.getPassword(), grantedAuthorities);
    }

    @Override
    public ApplicationUserDto findApplicationUserByEmail(String email) throws DtoNotFoundException {
        LOGGER.debug("Find application user by email");
        ApplicationUserDto applicationUser = userDao.findByEmail(email);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new DtoNotFoundException(String.format("Could not find the user with the email address %s", email));
    }

    @Override
    public String
    login(String email, String password) throws UserLockedException {
        try {
            checkBruteForceGuard(email);
        } catch (ExecutionException e) {
            throw new BadCredentialsException(
                ACCOUNT_LOCKED);
        } catch (EntityNotFoundException | SchedulerException e) {
            throw new BadCredentialsException(INCORRECT_USERNAME_OR_PASSWORD);
        }
        try {
            ApplicationUserDto applicationUserDto = findApplicationUserByEmail(email);
            if (!applicationUserDto.getAccountActivated()) {
                throw new BadCredentialsException("Bitte aktiviere dein Konto zuvor.");
            }
            UserDetails userDetails = getUserDetailsForUser(applicationUserDto);
            if (isValidLogin(password, userDetails, applicationUserDto)
            ) {
                loginAttemptCache.put(email, 1);
                List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
                return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
            }
        } catch (DtoNotFoundException e) {
            throw new BadCredentialsException(INCORRECT_USERNAME_OR_PASSWORD);
        }
        throw new BadCredentialsException(INCORRECT_USERNAME_OR_PASSWORD);
    }

    private void checkBruteForceGuard(String email)
        throws ExecutionException, EntityNotFoundException, SchedulerException, UserLockedException {
        Integer recentLoginAttempts = loginAttemptCache.get(email, () -> 1);
        if (recentLoginAttempts > 5) {
            ApplicationUserDto byEmail = userDao.findByEmail(email);
            if (byEmail != null) {
                byEmail.setAccountLocked(true);
                userDao.update(byEmail);
                userUnlockSchedulingService.scheduleUnlockUser(byEmail.getEmail());
            }
            throw new UserLockedException(
                ACCOUNT_LOCKED);
        }
        loginAttemptCache.put(email, recentLoginAttempts + 1);
    }

    private boolean isValidLogin(String password, UserDetails userDetails, ApplicationUserDto applicationUserDto) {
        return userDetails != null
            && userDetails.isAccountNonExpired()
            && userDetails.isAccountNonLocked()
            && userDetails.isCredentialsNonExpired()
            && applicationUserDto.getAccountActivated()
            && !applicationUserDto.isAccountLocked()
            && passwordEncoder.matches(password.concat(applicationUserDto.getSalt()),
            userDetails.getPassword());
    }

    public ApplicationUserDto createUser(ApplicationUserDto toCreate) throws ValidationException, ForbiddenException, MailNotSentException {
        LOGGER.debug("Create user");
        userValidator.validateForCreate(toCreate);


        AddressDto addressDto = addressService.create(toCreate.getAddress());
        toCreate.setAddress(addressDto);
        toCreate.setSalt(SecurityUtil.generateSalt(32));
        toCreate.setPassword(passwordEncoder.encode(toCreate.getPassword() + toCreate.getSalt()));
        ApplicationUserDto user = userDao.findByEmail(toCreate.getEmail());
        if (user != null) {
            try {
                toCreate.setId(user.getId());
                user = userDao.update(toCreate);
                // Invalidate old tokens for the current email

            } catch (EntityNotFoundException e) {
                throw new ValidationException("Fehler beim Erstellen des Benutzers.");
            }
        } else {
            user = userDao.create(toCreate);
        }

        // Create a token for email confirmation
        AccountActivateTokenDto accountActivateToken = createAndSaveAccountActivateToken(toCreate.getEmail());

        // Send email to the user to confirm the email address
        MailBody mailBody = generateMailBodyActivateAccount(toCreate, accountActivateToken.getToken());
        try {
            emailSenderService.sendHtmlMail(mailBody);
        } catch (MessagingException e) {
            throw new MailNotSentException("Error sending mail.");
        }
        return user;
    }

    @Override
    public Page<ApplicationUserDto> search(ApplicationUserSearchDto searchParameters) {
        LOGGER.debug("Search for users: {}", searchParameters);
        return userDao.search(searchParameters);
    }

    @Override
    public ApplicationUserDto updateUserStatusByEmail(ApplicationUserDto toUpdate, String adminEmail)
        throws DtoNotFoundException, ValidationException, MailNotSentException {
        LOGGER.debug("Update user status: {}", toUpdate);
        userValidator.validateForUpdateStatus(toUpdate.getEmail(), adminEmail);
        ApplicationUserDto user = userDao.findByEmail(toUpdate.getEmail());
        if (user == null) {
            throw new DtoNotFoundException("Could not update the user with the email address " + toUpdate.getEmail()
                + " because it does not exist");
        }
        if (user.getSuperAdmin()) {
            throw new ValidationException("Cannot update the status of this user.");
        }
        try {
            user.setAccountLocked(toUpdate.isAccountLocked());
            ApplicationUserDto updatedUser = userDao.update(user);
            if (!updatedUser.isAccountLocked()) {
                // Send email to the user to inform about the account unlock
                MailBody mailBody = generateMailBodyUnlockUser(updatedUser);
                emailSenderService.sendHtmlMail(mailBody);
            }
            return user;
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        } catch (MessagingException e) {
            throw new MailNotSentException("Error sending mail.", e);
        }
    }

    @Override
    public ApplicationUserDto updateUserInfo(ApplicationUserDto userInfo) throws ValidationException,
        MailNotSentException, DtoNotFoundException {
        LOGGER.debug("Update user info: {}", userInfo);
        ApplicationUserDto user;
        try {
            user = userDao.findById(userInfo.getId());
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException("Could not update the user because it does not exist.");
        }

        // Update phone number if it is not null or empty
        if (userInfo.getPhoneNumber() != null) {
            user.setPhoneNumber(userInfo.getPhoneNumber());
        }

        if (userInfo.getAddress() != null) {
            user.setAddress(userInfo.getAddress());
        }

        // Validate user information
        userValidator.validateForUpdate(user);

        // Update email if it is different from the current email
        if ((userInfo.getEmail() != null) && !Objects.equals(user.getEmail(),
            userInfo.getEmail())) {
            ApplicationUserDto userByEmail = null;
            try {
                userByEmail = findApplicationUserByEmail(userInfo.getEmail());
            } catch (DtoNotFoundException e) {
                // Do nothing
            }
            if (userByEmail != null) {
                throw new ValidationException("Die neue E-Mail-Adresse existiert bereits.");
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
            if (userInfo.getAddress() != null) {
                addressService.create(userInfo.getAddress());
            }
            return userDao.update(user);
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException("Could not update the user because it does not exist.");
        } catch (ForbiddenException e) {
            throw new ValidationException("Could not update the user because the address is invalid.");
        }
    }

    @Override
    public ApplicationUserDto findApplicationUserById(Long id) throws DtoNotFoundException {
        if (id != null) {
            try {
                return userDao.findById(id);
            } catch (EntityNotFoundException e) {
                throw new DtoNotFoundException("Could not find the user with the id " + id);
            }
        }
        return null;
    }

    @Override
    public ApplicationUserDto updateUserEmailWithValidToken(String token) throws ValidationException {
        EmailChangeTokenDto emailChangeToken = emailChangeTokenDao.findByToken(token);

        if (emailChangeToken == null) {
            throw new ValidationException("Der Link ist ungültig.");
        }

        if (emailChangeToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Dieser Link ist abgelaufen.");
        }

        ApplicationUserDto user = userDao.findByEmail(emailChangeToken.getNewEmail());
        if (user != null) {
            throw new ValidationException("Email Adresse existiert bereits.");
        }
        user = userDao.findByEmail(emailChangeToken.getCurrentEmail());
        user.setEmail(emailChangeToken.getNewEmail());
        ApplicationUserDto updatedUser = null;
        try {
            updatedUser = userDao.update(user);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Could not update the user with the email address {} because it does not exist.",
                emailChangeToken.getCurrentEmail());
        }
        invalidateEmailChangeTokens(emailChangeToken.getCurrentEmail());
        return updatedUser;
    }

    @Override
    public void sendEmailForNewPassword(String email, boolean reset) throws MailNotSentException {
        // Find the user by email
        ApplicationUserDto userByEmail;
        try {
            userByEmail = findApplicationUserByEmail(email);
        } catch (DtoNotFoundException e) {
            LOGGER.warn("Could not find the user with the email address {}", email);
            // Do nothing, attacker should not now if it worked
            return;
        }

        // Create a token for password reset
        NewPasswordTokenDto passwordResetToken = createAndSaveNewPasswordToken(email);

        MailBody mailBody;
        if (reset) {
            // Send email to the user to reset the password
            mailBody = generateMailBodyResetPassword(userByEmail, passwordResetToken.getToken());
        } else {
            // Send email to the user to change the password
            mailBody = generateMailBodyChangePassword(userByEmail, passwordResetToken.getToken());
        }
        try {
            emailSenderService.sendHtmlMail(mailBody);
        } catch (MessagingException e) {
            throw new MailNotSentException("Error sending mail.");
        }
    }

    @Override
    public void updatePassword(String token, String currentPassword, String newPassword) throws ValidationException,
        DtoNotFoundException {
        NewPasswordTokenDto validToken = newPasswordTokenDao.findByToken(token);
        if (validToken == null) {
            throw new ValidationException("Der Link ist ungültig.");
        }

        if (validToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Dieser Link ist abgelaufen.");
        }

        if (currentPassword != null) {
            ApplicationUserDto user = userDao.findByEmail(validToken.getEmail());
            if (!passwordEncoder.matches(currentPassword + user.getSalt(), user.getPassword())) {
                throw new ValidationException("Das alte Passwort ist falsch.");
            }
            if (currentPassword.equals(newPassword)) {
                throw new ValidationException("Das neue Passwort muss sich vom alten Passwort unterscheiden.");
            }
        }

        if (newPassword == null || newPassword.isEmpty()) {
            throw new ValidationException("Das Passwort darf nicht leer sein.");
        } else {
            if (newPassword.length() < 8) {
                throw new ValidationException("Das Passwort muss mindestens 8 Zeichen lang sein.");
            }
        }

        ApplicationUserDto user = userDao.findByEmail(validToken.getEmail());
        user.setPassword(passwordEncoder.encode(newPassword + user.getSalt()));
        try {
            userDao.update(user);
            invalidateNewPasswordTokens(validToken.getEmail());
        } catch (EntityNotFoundException e) {
            LOGGER.warn(e.getMessage());
            throw new DtoNotFoundException("Das Passwort konnte nicht aktualisiert werden, da der Benutzer nicht existiert.");
        }
    }

    @Override
    public void activateAccount(String token) throws ValidationException {
        LOGGER.debug("Activate account with token: {}", token);
        AccountActivateTokenDto emailConfirmToken = accountActivateTokenDao.findByToken(token);
        if (emailConfirmToken == null) {
            throw new ValidationException("Dieser Link ist ungültig.");
        }

        if (emailConfirmToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Dieser Link ist abgelaufen. Bitte neu registrieren.");
        }

        try {
            ApplicationUserDto user = userDao.findByEmail(emailConfirmToken.getEmail());
            user.setAccountActivated(true);
            userDao.update(user);
            accountActivateTokenDao.deleteById(emailConfirmToken.getId());
        } catch (EntityNotFoundException e) {
            LOGGER.error("Could not confirm the email because the user does not exist.", e);
        }
    }

    @Override
    public void deleteUser(long id) throws DtoNotFoundException, MailNotSentException {
        //validate user
        ApplicationUserDto user;
        try {
            user = userDao.findById(id);
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e.getMessage());
        }

        try {
            userDao.deleteById(id);

        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e.getMessage());
        }

        // invalidate all tokens for the user
        invalidateEmailChangeTokens(user.getEmail());
        invalidateNewPasswordTokens(user.getEmail());
        invalidateAccountActivateTokens(user.getEmail());

        MailBody mailBody = generateMailBodyAccountDelete(user);
        try {
            emailSenderService.sendHtmlMail(mailBody);
        } catch (MessagingException e) {
            throw new MailNotSentException("Error sending mail.");
        }
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
        invalidateEmailChangeTokens(currentEmail);

        return emailChangeTokenDao.create(emailChangeToken);
    }

    private NewPasswordTokenDto createAndSaveNewPasswordToken(String email) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(MAX_EXPIRATION_TIME);

        NewPasswordTokenDto newPasswordToken = new NewPasswordTokenDto();
        newPasswordToken.setToken(token);
        newPasswordToken.setEmail(email);
        newPasswordToken.setExpiryDate(expiryDate);

        // Invalidate old tokens for the current email
        invalidateNewPasswordTokens(email);

        return newPasswordTokenDao.create(newPasswordToken);
    }

    private AccountActivateTokenDto createAndSaveAccountActivateToken(String email) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(MAX_EXPIRATION_TIME);

        AccountActivateTokenDto emailConfirmToken = new AccountActivateTokenDto();
        emailConfirmToken.setToken(token);
        emailConfirmToken.setEmail(email);
        emailConfirmToken.setExpiryDate(expiryDate);

        invalidateAccountActivateTokens(email);

        return accountActivateTokenDao.create(emailConfirmToken);
    }

    private void invalidateNewPasswordTokens(String email) {
        newPasswordTokenDao.findByEmail(email).forEach(token -> {
            token.setExpiryDate(LocalDateTime.now().minusMinutes(1));
            try {
                newPasswordTokenDao.update(token);  // Save the updated token
            } catch (EntityNotFoundException e) {
                LOGGER.warn("Could not update the password reset token because it does not exist.", e);
            }
        });
    }

    private void invalidateAccountActivateTokens(String email) {
        accountActivateTokenDao.findByEmail(email).forEach(token -> {
            token.setExpiryDate(LocalDateTime.now().minusMinutes(1));
            try {
                accountActivateTokenDao.update(token);  // Save the updated token
            } catch (EntityNotFoundException e) {
                LOGGER.warn("Could not update the account activate token because it does not exist.", e);
            }
        });
    }

    private void invalidateEmailChangeTokens(String currentEmail) {
        emailChangeTokenDao.findByCurrentEmail(currentEmail).forEach(token -> {
            token.setExpiryDate(LocalDateTime.now().minusMinutes(1));
            try {
                emailChangeTokenDao.update(token);  // Save the updated token
            } catch (EntityNotFoundException e) {
                LOGGER.warn("Could not update the email change token because it does not exist.", e);
            }
        });
    }

    private MailBody generateMailBodyChangeEmail(ApplicationUserDto userInfo, ApplicationUserDto user, String token) {
        String email = userInfo.getEmail();
        String subject = "Ihre E-Mail Adresse wurde geändert.";
        String url = "http://localhost:4200/#/user/update/email?token=" + token;

        String emailTemplate = """
            <!DOCTYPE html>
            <html lang="de">
            <head>
               <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
               <style>
                  body { background-color: #f8f9fa; font-family: Arial, sans-serif; }
                  .container { width: 100%; max-width: 600px; margin: 0 auto; padding: 20px; }
                  .card { background-color: #ffffff; border: 1px solid #dee2e6; border-radius: 0.25rem; padding: 20px; }
                  .card-body { padding: 20px; }
                  .h3 { font-size: 1.75rem; margin-bottom: 0.5rem; }
                  .h5 { font-size: 1.25rem; }
                  .text-gray-700 { color: #6c757d; }
                  .btn-primary { display: inline-block; font-weight: 400; color: #fff !important; text-align: center; vertical-align: middle; cursor: pointer; background-color: #007bff; border: 1px solid #007bff; padding: 0.375rem 0.75rem; font-size: 1rem; border-radius: 0.25rem; text-decoration: none; }
               </style>
            </head>
            <body>
                <div class="container">
                  <div class="card my-10">
                    <div class="card-body">
                      <h1 class="h3">Bestätigung E-Mail Änderung</h1>
                      <h5 class="h5">Hallo""" + " " + user.getFirstName() +
            """
                !</h5>
                <hr>
                <div>
                  <p class="text-gray-700">Wir haben deine Anfrage zur Änderung deiner E-Mail Adresse zu""" + " " + userInfo.getEmail() + " " +
            """
                erhalten.</p>
                <p class="text-gray-700">Falls du deine E-Mail-Adresse nicht ändern und weiterhin die aktuelle E-Mail-Adresse""" + " " + user.getEmail() + " " +
            """
                  verwenden möchtest, ignoriere einfach diese E-Mail.</p>
                  <p class="text-gray-700">Bis du diese Änderung bestätigst, musst du deine aktuelle E-Mail-Adresse verwenden, um dich bei TicketLine anzumelden.</p>
                <hr>
                <a class="btn btn-primary" href=\"""" + url + "\"" +
            """
                "target="_blank" style="color: #fff !important;">E-Mail Adresse bestätigen</a>
                      <p class="text-gray-700">Dieser Link ist für die nächsten""" + " " + MAX_EXPIRATION_TIME + " " +
            """
                          Minuten gültig.</p>
                          <p class="text-gray-700">Dies ist eine automatisch generierte E-Mail – bitte antworte nicht auf diese E-Mail.</p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </body>
                </html>
                """;
        return new MailBody(email, subject, emailTemplate);
    }

    private MailBody generateMailBodyResetPassword(ApplicationUserDto user, String token) {
        String email = user.getEmail();
        String subject = "Passwort zurücksetzen";
        String url = "http://localhost:4200/#/user/password/reset?token=" + token;

        String emailTemplate = """
            <!DOCTYPE html>
            <html lang="de">
            <head>
               <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
               <style>
                  body { background-color: #f8f9fa; font-family: Arial, sans-serif; }
                  .container { width: 100%; max-width: 600px; margin: 0 auto; padding: 20px; }
                  .card { background-color: #ffffff; border: 1px solid #dee2e6; border-radius: 0.25rem; padding: 20px; }
                  .card-body { padding: 20px; }
                  .h3 { font-size: 1.75rem; margin-bottom: 0.5rem; }
                  .h5 { font-size: 1.25rem; }
                  .text-gray-700 { color: #6c757d; }
                  .btn-primary { display: inline-block; font-weight: 400; color: #fff !important; text-align: center; vertical-align: middle; cursor: pointer; background-color: #007bff; border: 1px solid #007bff; padding: 0.375rem 0.75rem; font-size: 1rem; border-radius: 0.25rem; text-decoration: none; }
               </style>
            </head>
            <body>
                <div class="container">
                  <div class="card my-10">
                    <div class="card-body">
                      <h1 class="h3">Passwort zurücksetzen</h1>
                      <h5 class="h5">Hallo""" + " " + user.getFirstName() +
            """
                !</h5>
                <hr>
                <div>
                  <p class="text-gray-700">Wir haben eine Anfrage erhalten, dass du dein Passwort für dein TicketLine-Konto zurücksetzen möchtest.</p>
                  <p class="text-gray-700">Wenn du diese Anfrage nicht gestellt hast, kannst du diese E-Mail ignorieren.</p>
                  <p class="text-gray-700">Um dein Passwort zurückzusetzen, klicke bitte auf den folgenden Button:</p>
                <hr>
                <a class="btn btn-primary" href=\"""" + url + "\"" +
            """
                target="_blank" style="color: #fff !important;">Passwort zurücksetzen</a>
                <p class="text-gray-700">Dieser Link ist für die nächsten""" + " " + MAX_EXPIRATION_TIME +
            """
                            Minuten gültig.</p>
                            <p class="text-gray-700">Dies ist eine automatisch generierte E-Mail – bitte antworte nicht auf diese E-Mail.</p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </body>
                </html>
                """;
        return new MailBody(email, subject, emailTemplate);
    }

    private MailBody generateMailBodyChangePassword(ApplicationUserDto user, String token) {
        String email = user.getEmail();
        String subject = "Passwort ändern";
        String url = "http://localhost:4200/#/user/password/change?token=" + token;

        String emailTemplate = """
            <!DOCTYPE html>
            <html lang="de">
            <head>
               <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
               <style>
                  body { background-color: #f8f9fa; font-family: Arial, sans-serif; }
                  .container { width: 100%; max-width: 600px; margin: 0 auto; padding: 20px; }
                  .card { background-color: #ffffff; border: 1px solid #dee2e6; border-radius: 0.25rem; padding: 20px; }
                  .card-body { padding: 20px; }
                  .h3 { font-size: 1.75rem; margin-bottom: 0.5rem; }
                  .h5 { font-size: 1.25rem; }
                  .text-gray-700 { color: #6c757d; }
                  .btn-primary { display: inline-block; font-weight: 400; color: #fff !important; text-align: center; vertical-align: middle; cursor: pointer; background-color: #007bff; border: 1px solid #007bff; padding: 0.375rem 0.75rem; font-size: 1rem; border-radius: 0.25rem; text-decoration: none; }
               </style>
            </head>
            <body>
                <div class="container">
                  <div class="card my-10">
                    <div class="card-body">
                      <h1 class="h3">Passwort ändern</h1>
                      <h5 class="h5">Hallo""" + " " + user.getFirstName() +
            """
                !</h5>
                <hr>
                <div>
                  <p class="text-gray-700">Wir haben eine Anfrage erhalten, dass du dein Passwort für dein TicketLine-Konto ändern möchtest.</p>
                  <p class="text-gray-700">Falls diese Aktion nicht von dir gestartet wurde, kontaktiere uns unter folgender E-Mail: ***REMOVED***@gmail.com. Falls schon, folge der weiteren Beschreibung um dein Passwort zu ändern.</p>
                  <p class="text-gray-700">Um dein Passwort zu ändern, klicke bitte auf den folgenden Button:</p>
                <hr>
                <a class="btn btn-primary" href=\"""" + url + "\"" +
            """
                target="_blank" style="color: #fff !important;">Passwort ändern</a>
                <p class="text-gray-700">Dieser Link ist für die nächsten""" + " " + MAX_EXPIRATION_TIME +
            """
                            Minuten gültig.</p>
                            <p class="text-gray-700">Dies ist eine automatisch generierte E-Mail – bitte antworte nicht auf diese E-Mail.</p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </body>
                </html>
                """;
        return new MailBody(email, subject, emailTemplate);
    }

    private MailBody generateMailBodyActivateAccount(ApplicationUserDto user, String token) {
        String email = user.getEmail();
        String subject = "Konto Aktivierung";
        String url = "http://localhost:4200/#/user/activate/account?token=" + token;

        String emailTemplate = """
            <!DOCTYPE html>
            <html lang="de">
            <head>
               <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
               <style>
                  body { background-color: #f8f9fa; font-family: Arial, sans-serif; }
                  .container { width: 100%; max-width: 600px; margin: 0 auto; padding: 20px; }
                  .card { background-color: #ffffff; border: 1px solid #dee2e6; border-radius: 0.25rem; padding: 20px; }
                  .card-body { padding: 20px; }
                  .h3 { font-size: 1.75rem; margin-bottom: 0.5rem; }
                  .h5 { font-size: 1.25rem; }
                  .text-gray-700 { color: #6c757d; }
                  .btn-primary { display: inline-block; font-weight: 400; color: #fff !important; text-align: center; vertical-align: middle; cursor: pointer; background-color: #007bff; border: 1px solid #007bff; padding: 0.375rem 0.75rem; font-size: 1rem; border-radius: 0.25rem; text-decoration: none; }
               </style>
            </head>
            <body>
                <div class="container">
                  <div class="card my-10">
                    <div class="card-body">
                      <h1 class="h3">Konto Aktivierung</h1>
                      <h5 class="h5">Hallo""" + " " + user.getFirstName() + "!</h5>" +
            """
                      <hr>
                      <div>
                      <p class="text-gray-700">Herzlich Willkommen bei TicketLine!
                      Du bist nur noch einen Schritt davon entfernt, dein Konto zu aktivieren.</p>
                      Bitte klicke auf den Link um dein TicketLine Konto zu aktivieren.</p>
                <hr>
                <a class="btn btn-primary" href=\"""" + url + "\"" +
            """
                target="_blank" style="color: #fff !important;">Konto aktivieren</a>
                <p class="text-gray-700">Dieser Link ist für die nächsten""" + " " + MAX_EXPIRATION_TIME + " " +
            """
                Minuten gültig.</p>
                            <p class="text-gray-700">Dies ist eine automatisch generierte E-Mail – bitte antworte nicht auf diese E-Mail.</p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </body>
                </html>
                """;
        return new MailBody(email, subject, emailTemplate);
    }

    private MailBody generateMailBodyAccountDelete(ApplicationUserDto user) {
        String email = user.getEmail();
        String subject = "Konto löschen";
        String url = "http://localhost:4200/";
        String emailTemplate = """
            <!DOCTYPE html>
            <html lang="de">
            <head>
               <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
               <style>
                  body { background-color: #f8f9fa; font-family: Arial, sans-serif; }
                  .container { width: 100%; max-width: 600px; margin: 0 auto; padding: 20px; }
                  .card { background-color: #ffffff; border: 1px solid #dee2e6; border-radius: 0.25rem; padding: 20px; }
                  .card-body { padding: 20px; }
                  .h3 { font-size: 1.75rem; margin-bottom: 0.5rem; }
                  .h5 { font-size: 1.25rem; }
                  .text-gray-700 { color: #6c757d; }
                  .btn-primary { display: inline-block; font-weight: 400; color: #fff !important; text-align: center; vertical-align: middle; cursor: pointer; background-color: #007bff; border: 1px solid #007bff; padding: 0.375rem 0.75rem; font-size: 1rem; border-radius: 0.25rem; text-decoration: none; }
               </style>
            </head>
            <body>
                <div class="container">
                  <div class="card my-10">
                    <div class="card-body">
                      <h1 class="h3">Konto löschen</h1>
                      <h5 class="h5">Hallo""" + " " + user.getFirstName() +
            """
                !</h5>
                <p class="text-gray-700">Hiermit bestätigen wir, dass dein Konto gelöscht wurde.</p>
                <p class="text-gray-700">Wir bedauern es sehr, dass du uns verlässt und hoffen, dass du uns in Zukunft wieder besuchst.</p>
                <a class="btn btn-primary" href=\"""" + url + "\"" +
            """
                target="_blank" style="color: #fff !important;">TicketLine Homepage</a>
                    </div>
                  </div>
                </body>
                """;
        return new MailBody(email, subject, emailTemplate);
    }

    private MailBody generateMailBodyUnlockUser(ApplicationUserDto user) {
        String email = user.getEmail();
        String subject = "Benutzerkonto entsperrt";
        String url = "http://localhost:4200/#/login";

        String emailTemplate = """
            <!DOCTYPE html>
            <html lang="de">
            <head>
               <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
               <style>
                  body { background-color: #f8f9fa; font-family: Arial, sans-serif; }
                  .container { width: 100%; max-width: 600px; margin: 0 auto; padding: 20px; }
                  .card { background-color: #ffffff; border: 1px solid #dee2e6; border-radius: 0.25rem; padding: 20px; }
                  .card-body { padding: 20px; }
                  .h3 { font-size: 1.75rem; margin-bottom: 0.5rem; }
                  .h5 { font-size: 1.25rem; }
                  .text-gray-700 { color: #6c757d; }
                  .btn-primary { display: inline-block; font-weight: 400; color: #fff !important; text-align: center; vertical-align: middle; cursor: pointer; background-color: #007bff; border: 1px solid #007bff; padding: 0.375rem 0.75rem; font-size: 1rem; border-radius: 0.25rem; text-decoration: none; }
               </style>
            </head>
            <body>
                <div class="container">
                  <div class="card my-10">
                    <div class="card-body">
                      <h1 class="h3">Benutzerkonto entsperrt</h1>
                      <h5 class="h5">Hallo""" + " " + user.getFirstName() +
            """
                !</h5>
                <hr>
                <div>
                  <p class="text-gray-700">Dein Benutzerkonto wurde erfolgreich von einem Administrator entsperrt.</p>
                  <p class="text-gray-700">Du kannst dich jetzt wieder bei deinem TicketLine-Konto anmelden und alle Funktionen nutzen.</p>
                  <p class="text-gray-700">Um dich anzumelden, klicke bitte auf den folgenden Button:</p>
                <hr>
                <a class="btn btn-primary" href=\"""" + url + "\"" +
            """
                target="_blank" style="color: #fff !important;">Zur Anmeldeseite</a>
                            <p class="text-gray-700">Dies ist eine automatisch generierte E-Mail – bitte antworte nicht auf diese E-Mail.</p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </body>
                </html>
                """;
        return new MailBody(email, subject, emailTemplate);
    }

}
