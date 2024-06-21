package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.AccountActivateTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EmailChangeTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.MailBody;
import at.ac.tuwien.sepr.groupphase.backend.dto.NewPasswordTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.AccountActivateTokenDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.EmailChangeTokenDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.NewPasswordTokenDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.UserDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.AddressService;
import at.ac.tuwien.sepr.groupphase.backend.service.EmailSenderService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.MailNotSentException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.UserValidator;
import at.ac.tuwien.sepr.groupphase.backend.supplier.ApplicationUserSupplier;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {


    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenizer jwtTokenizer;

    @Mock
    private UserValidator userValidator;

    @Mock
    private AddressService addressService;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private EmailChangeTokenDao emailChangeTokenDao;

    @Mock
    private NewPasswordTokenDao newPasswordTokenDao;

    @Mock
    private AccountActivateTokenDao accountActivateTokenDao;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<ApplicationUserDto> userDto;

    @Test
    void createUserShouldCallValidateAndSetSalt() throws Exception {
        ApplicationUserDto user = ApplicationUserSupplier.anAdminUser();
        doNothing().when(userValidator).validateForCreate(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(addressService.create(user.getAddress())).thenReturn(user.getAddress());

        AccountActivateTokenDto emailConfirmToken = new AccountActivateTokenDto();
        emailConfirmToken.setToken(UUID.randomUUID().toString());
        emailConfirmToken.setEmail(user.getEmail());
        emailConfirmToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        when(accountActivateTokenDao.create(any(AccountActivateTokenDto.class))).thenReturn(emailConfirmToken);

        userService.createUser(user);

        verify(userValidator).validateForCreate(user);
        verify(userDao).create(userDto.capture());
        verify(addressService).create(user.getAddress());
        assertNotNull(userDto.getValue().getSalt());
        assertNotNull(userDto.getValue().getPassword());
    }

    @Test
    void createUserShouldThrowValidationExceptionWhenEmailConfirmationTokenExists() throws Exception {
        // Arrange
        ApplicationUserDto userToCreate = ApplicationUserSupplier.anAdminUser();
        String email = userToCreate.getEmail();

        when(accountActivateTokenDao.findByEmail(email)).thenReturn(Collections.singletonList(new AccountActivateTokenDto()));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.createUser(userToCreate);
        });

        assertEquals("Es wurde bereits eine Bestätigungsmail an diese E-Mail-Adresse gesendet.", exception.getMessage());

        // Verify that no further interactions occurred after the exception
        verify(userValidator).validateForCreate(userToCreate);
        verify(accountActivateTokenDao).findByEmail(email);
        verifyNoMoreInteractions(userValidator, addressService, passwordEncoder, emailSenderService, userDao, accountActivateTokenDao);
    }

    @Test
    void searchShouldReturnCorrectResults() {
        ApplicationUserSearchDto searchParams = new ApplicationUserSearchDto("Berta", "Muster", "admin@email.com",
            false, PageRequest.of(0, 10));
        Page<ApplicationUserDto> mockUsers = new PageImpl<>(List.of(ApplicationUserSupplier.anAdminUser(), ApplicationUserSupplier.anAdminUser()),
            PageRequest.of(0, 10), 2);

        when(userDao.search(searchParams)).thenReturn(mockUsers);

        Page<ApplicationUserDto> result = userService.search(searchParams);

        verify(userDao).search(searchParams);
        assertEquals(mockUsers, result);
    }

    @Test
    void updateUserStatusByEmailShouldUpdateUser() throws Exception {
        ApplicationUserDto userToUpdate = ApplicationUserSupplier.anAdminUser();
        userToUpdate.setEmail("update@email.com");
        String adminEmail = "admin@email.com";

        doNothing().when(userValidator).validateForUpdateStatus(userToUpdate.getEmail(), adminEmail);
        when(userDao.findByEmail(userToUpdate.getEmail())).thenReturn(userToUpdate);
        when(userDao.update(userToUpdate)).thenReturn(userToUpdate);

        ApplicationUserDto updatedUser = userService.updateUserStatusByEmail(userToUpdate, adminEmail);
    }

    @Test
    void updateUserStatusByEmailShouldThrowNotFoundExceptionIfUserDoesNotExist() throws Exception {
        ApplicationUserDto userToUpdate = ApplicationUserSupplier.anAdminUser();
        userToUpdate.setEmail("nonexistent@email.com");
        String adminEmail = "admin@email.com";

        doNothing().when(userValidator).validateForUpdateStatus(userToUpdate.getEmail(), adminEmail);
        when(userDao.findByEmail(userToUpdate.getEmail())).thenReturn(null);

        DtoNotFoundException exception = assertThrows(DtoNotFoundException.class, () -> {
            userService.updateUserStatusByEmail(userToUpdate, adminEmail);
        });

        verify(userValidator).validateForUpdateStatus(userToUpdate.getEmail(), adminEmail);
        verify(userDao).findByEmail(userToUpdate.getEmail());
        assertEquals("Could not update the user with the email address nonexistent@email.com because it does not exist", exception.getMessage());
    }

    @Test
    void updateUserStatusByEmailShouldThrowNotFoundExceptionOnEntityNotFoundException() throws Exception {
        ApplicationUserDto userToUpdate = ApplicationUserSupplier.anAdminUser();
        userToUpdate.setEmail("update@email.com");
        String adminEmail = "admin@email.com";

        doNothing().when(userValidator).validateForUpdateStatus(userToUpdate.getEmail(), adminEmail);
        when(userDao.findByEmail(userToUpdate.getEmail())).thenReturn(userToUpdate);
        when(userDao.update(userToUpdate)).thenThrow(new EntityNotFoundException(1L));

        DtoNotFoundException exception = assertThrows(DtoNotFoundException.class, () -> {
            userService.updateUserStatusByEmail(userToUpdate, adminEmail);
        });

        verify(userValidator).validateForUpdateStatus(userToUpdate.getEmail(), adminEmail);
        verify(userDao).findByEmail(userToUpdate.getEmail());
        verify(userDao).update(userToUpdate);
        assertInstanceOf(EntityNotFoundException.class, exception.getCause());
    }

    @Test
    void updateUserStatusByEmailShouldThrowValidationExceptionWhenUserIsSuperAdmin() throws Exception {
        // Prepare the data
        ApplicationUserDto userToUpdate = ApplicationUserSupplier.anAdminUser();
        userToUpdate.setEmail("superadmin@example.com");
        userToUpdate.setAccountLocked(true);
        userToUpdate.setSuperAdmin(true); // Set superAdmin to true

        String adminEmail = "admin@example.com";

        // Mocking the behavior of userValidator and userDao
        doNothing().when(userValidator).validateForUpdateStatus(userToUpdate.getEmail(), adminEmail);
        when(userDao.findByEmail(userToUpdate.getEmail())).thenReturn(userToUpdate);

        // Perform the update and assert that a ValidationException is thrown
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.updateUserStatusByEmail(userToUpdate, adminEmail);
        });

        // Assert that the exception message is as expected
        assertEquals("Cannot update the status of this user.", exception.getMessage());

        // Verify that update was never called
        verify(userDao, never()).update(any(ApplicationUserDto.class));
    }

    @Test
    void updateUserInfoShouldThrowValidationExceptionIfEmailAlreadyInUse() throws Exception {
        var user = new ApplicationUserDto();
        user.setId(1L);
        user.setEmail("current@email.com");
        user.setPhoneNumber("+436603333333");

        var emailChangeToken = new EmailChangeTokenDto();
        emailChangeToken.setToken(UUID.randomUUID().toString());
        emailChangeToken.setNewEmail("new@email.com");
        emailChangeToken.setCurrentEmail("current@email.com");
        emailChangeToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));


        ApplicationUserDto userInfo = new ApplicationUserDto();
        userInfo.setId(1L);
        userInfo.setEmail("new@email.com");

        when(userDao.findById(1L)).thenReturn(user);
        when(userDao.findByEmail("new@email.com")).thenReturn(new ApplicationUserDto());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.updateUserInfo(userInfo);
        });

        assertEquals("The new email address is already in use.", exception.getMessage());
    }

    @Test
    void updateUserInfoShouldThrowMailNotSentExceptionIfEmailSendingFails() throws Exception {
        var user = new ApplicationUserDto();
        user.setId(1L);
        user.setEmail("current@email.com");
        user.setPhoneNumber("+436603333333");

        var emailChangeToken = new EmailChangeTokenDto();
        emailChangeToken.setToken(UUID.randomUUID().toString());
        emailChangeToken.setNewEmail("new@email.com");
        emailChangeToken.setCurrentEmail("current@email.com");
        emailChangeToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        ApplicationUserDto userInfo = new ApplicationUserDto();
        userInfo.setId(1L);
        userInfo.setEmail("new@email.com");

        when(userDao.findById(1L)).thenReturn(user);
        when(userDao.findByEmail("new@email.com")).thenReturn(null);
        when(emailChangeTokenDao.create(any(EmailChangeTokenDto.class))).thenReturn(emailChangeToken);

        doThrow(new MessagingException("Error sending mail")).when(emailSenderService).sendHtmlMail(any(MailBody.class));

        MailNotSentException exception = assertThrows(MailNotSentException.class, () -> {
            userService.updateUserInfo(userInfo);
        });

        assertEquals("Error sending mail.", exception.getMessage());
    }

    @Test
    void updateUserInfoShouldThrowNotFoundExceptionIfUserDoesNotExist() throws Exception {
        ApplicationUserDto userInfo = new ApplicationUserDto();
        userInfo.setId(1L);
        userInfo.setEmail("new@email.com");

        when(userDao.findById(1L)).thenThrow(new EntityNotFoundException(1L));

        DtoNotFoundException exception = assertThrows(DtoNotFoundException.class, () -> {
            userService.updateUserInfo(userInfo);
        });

        assertEquals("Could not update the user because it does not exist.", exception.getMessage());
    }

    @Test
    void updateUserInfoShouldUpdatePhoneNumberWhenNotNull() throws Exception {
        // Prepare the data
        ApplicationUserDto userToUpdate = ApplicationUserSupplier.anAdminUser();
        userToUpdate.setId(1L);
        userToUpdate.setPhoneNumber("+436603333333");

        ApplicationUserDto updatedInfo = ApplicationUserSupplier.anAdminUser();
        updatedInfo.setId(1L);
        updatedInfo.setPhoneNumber("+436603333332");

        // Mocking the behavior of userDao and emailChangeTokenDao
        when(userDao.findById(1L)).thenReturn(userToUpdate);
        when(userDao.update(userToUpdate)).thenReturn(updatedInfo);

        // Perform the update
        ApplicationUserDto updatedUser = userService.updateUserInfo(updatedInfo);

        // Assert that the phone number is updated
        assertEquals("+436603333332", updatedUser.getPhoneNumber());

        // Verify no email was sent since email was not updated
        verify(emailSenderService, never()).sendHtmlMail(any(MailBody.class));
    }

    @Test
    void updateUserInfoShouldThrowValidationExceptionWhenEmailIsNull() throws Exception {
        // Prepare the data
        ApplicationUserDto userToUpdate = ApplicationUserSupplier.anAdminUser();
        userToUpdate.setId(1L);
        userToUpdate.setPhoneNumber("+436603333333");

        ApplicationUserDto updatedInfo = ApplicationUserSupplier.anAdminUser();
        updatedInfo.setId(1L);
        updatedInfo.setPhoneNumber("+436603333332");
        updatedInfo.setEmail(null); // Setting email to null

        // Mocking the behavior of userDao
        when(userDao.findById(1L)).thenReturn(userToUpdate);
        when(userDao.update(userToUpdate)).thenReturn(updatedInfo);

        // Perform the update
        ApplicationUserDto updatedUser = userService.updateUserInfo(updatedInfo);

        // Assert that the phone number is updated
        assertEquals("+436603333332", updatedUser.getPhoneNumber());

        // Verify that update and email sending methods were never called
        verify(emailSenderService, never()).sendHtmlMail(any(MailBody.class));
    }

    @Test
    void updateUserEmailWithValidTokenShouldUpdateEmailAndReturnUpdatedUser() throws Exception {
        // Arrange
        String token = UUID.randomUUID().toString();
        EmailChangeTokenDto emailChangeToken = new EmailChangeTokenDto();
        emailChangeToken.setToken(token);
        emailChangeToken.setNewEmail("new@email.com");
        emailChangeToken.setCurrentEmail("current@email.com");
        emailChangeToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        ApplicationUserDto user = new ApplicationUserDto();
        user.setEmail("current@email.com");

        when(emailChangeTokenDao.findByToken(token)).thenReturn(emailChangeToken);
        when(userDao.findByEmail("current@email.com")).thenReturn(user);
        when(userDao.update(user)).thenReturn(user);

        // Act
        ApplicationUserDto result = userService.updateUserEmailWithValidToken(token);

        // Assert
        verify(userDao).findByEmail("current@email.com");
        verify(userDao).update(user);
        verify(emailChangeTokenDao).findByToken(token);
        verify(emailChangeTokenDao, times(1)).findByToken(token);
        assertNotNull(result);
        assertEquals("new@email.com", result.getEmail());
    }

    @Test
    void updateUserEmailWithValidTokenShouldReturnNullIfTokenIsNull() {
        // Arrange
        String token = null;

        // Act
        ApplicationUserDto result = userService.updateUserEmailWithValidToken(token);

        // Assert
        verify(emailChangeTokenDao, times(1)).findByToken(token);
        assertNull(result);
    }

    @Test
    void updateUserEmailWithValidTokenShouldReturnNullIfTokenIsExpired() {
        // Arrange
        String token = UUID.randomUUID().toString();
        EmailChangeTokenDto emailChangeToken = new EmailChangeTokenDto();
        emailChangeToken.setToken(token);
        emailChangeToken.setNewEmail("new@email.com");
        emailChangeToken.setCurrentEmail("current@email.com");
        emailChangeToken.setExpiryDate(LocalDateTime.now().minusMinutes(10)); // Expired token

        when(emailChangeTokenDao.findByToken(token)).thenReturn(emailChangeToken);

        // Act
        ApplicationUserDto result = userService.updateUserEmailWithValidToken(token);

        // Assert
        verify(emailChangeTokenDao, times(1)).findByToken(token);
        assertNull(result);
    }

    @Test
    void updateUserEmailWithValidTokenShouldLogErrorAndReturnNullIfUserNotFound() throws Exception {
        // Arrange
        String token = UUID.randomUUID().toString();
        EmailChangeTokenDto emailChangeToken = new EmailChangeTokenDto();
        emailChangeToken.setToken(token);
        emailChangeToken.setNewEmail("new@email.com");
        emailChangeToken.setCurrentEmail("current@email.com");
        emailChangeToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        ApplicationUserDto user = new ApplicationUserDto();
        user.setEmail("current@email.com");

        when(emailChangeTokenDao.findByToken(token)).thenReturn(emailChangeToken);
        when(userDao.findByEmail("current@email.com")).thenReturn(user);
        when(userDao.update(user)).thenThrow(new EntityNotFoundException(1L));

        // Act
        ApplicationUserDto result = userService.updateUserEmailWithValidToken(token);

        // Assert
        verify(userDao).findByEmail("current@email.com");
        verify(userDao).update(user);
        assertNull(result);
    }

    @Test
    void updateUserEmailWithValidTokenShouldInvalidateOldTokens() throws Exception {
        // Arrange
        String token = UUID.randomUUID().toString();
        EmailChangeTokenDto emailChangeToken = new EmailChangeTokenDto();
        emailChangeToken.setToken(token);
        emailChangeToken.setNewEmail("new@email.com");
        emailChangeToken.setCurrentEmail("current@email.com");
        emailChangeToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        ApplicationUserDto user = new ApplicationUserDto();
        user.setEmail("current@email.com");

        when(emailChangeTokenDao.findByToken(token)).thenReturn(emailChangeToken);
        when(userDao.findByEmail("current@email.com")).thenReturn(user);
        when(userDao.update(user)).thenReturn(user);

        // Act
        ApplicationUserDto result = userService.updateUserEmailWithValidToken(token);

        // Assert
        verify(userDao).findByEmail("current@email.com");
        verify(userDao).update(user);
        verify(emailChangeTokenDao, times(1)).findByToken(token);
        verify(emailChangeTokenDao, times(1)).findByCurrentEmail("current@email.com");
        assertNotNull(result);
        assertEquals("new@email.com", result.getEmail());
    }

    @Test
    void sendEmailForNewPasswordShouldLogWarningAndReturnIfUserNotFound() throws MessagingException {
        // Arrange
        String email = "nonexistent@example.com";

        // Mock the behavior of userDao to return null
        when(userDao.findByEmail(email)).thenReturn(null);

        // Act and Assert
        Assertions.assertDoesNotThrow(() -> userService.sendEmailForNewPassword(email, true));

        // Verify that no email was sent
        verify(emailSenderService, never()).sendHtmlMail(any(MailBody.class));
    }

    @Test
    void sendEmailForNewPasswordShouldThrowMailNotSentExceptionWhenEmailFails() throws Exception {
        // Arrange
        String email = "test@example.com";
        ApplicationUserDto user = ApplicationUserSupplier.anAdminUser();
        user.setEmail(email);

        NewPasswordTokenDto newToken = new NewPasswordTokenDto();
        newToken.setToken("newToken");
        newToken.setEmail(email);
        newToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        when(userDao.findByEmail(email)).thenReturn(user);
        when(newPasswordTokenDao.findByEmail(email)).thenReturn(Collections.emptyList());
        when(newPasswordTokenDao.create(any(NewPasswordTokenDto.class))).thenReturn(newToken);
        doThrow(new MessagingException("Error sending mail")).when(emailSenderService).sendHtmlMail(any(MailBody.class));

        // Act & Assert
        MailNotSentException exception = assertThrows(MailNotSentException.class, () -> {
            userService.sendEmailForNewPassword(email, true);
        });

        assertEquals("Error sending mail.", exception.getMessage());
        verify(newPasswordTokenDao).findByEmail(email);
        verify(newPasswordTokenDao).create(any(NewPasswordTokenDto.class));
        verify(emailSenderService).sendHtmlMail(any(MailBody.class));
    }

    @Test
    void sendEmailForResetPasswordWhenUserFoundShouldSendEmail() throws Exception {
        String email = "test@example.com";
        ApplicationUserDto user = new ApplicationUserDto();
        user.setEmail(email);
        when(userDao.findByEmail(email)).thenReturn(user);

        String token = "token";
        NewPasswordTokenDto newPasswordTokenDto = new NewPasswordTokenDto();
        newPasswordTokenDto.setToken(token);
        when(newPasswordTokenDao.create(Mockito.any(NewPasswordTokenDto.class))).thenReturn(newPasswordTokenDto);

        userService.sendEmailForNewPassword(email, true);

        verify(userDao).findByEmail(email);
        verify(newPasswordTokenDao).create(Mockito.any(NewPasswordTokenDto.class));
        verify(emailSenderService).sendHtmlMail(Mockito.any(MailBody.class));
    }

    @Test
    void sendEmailForChangePasswordWhenUserFoundShouldSendEmail() throws Exception {
        String email = "test@example.com";
        ApplicationUserDto user = new ApplicationUserDto();
        user.setEmail(email);
        when(userDao.findByEmail(email)).thenReturn(user);

        String token = "token";
        NewPasswordTokenDto newPasswordTokenDto = new NewPasswordTokenDto();
        newPasswordTokenDto.setToken(token);
        when(newPasswordTokenDao.create(Mockito.any(NewPasswordTokenDto.class))).thenReturn(newPasswordTokenDto);

        userService.sendEmailForNewPassword(email, false);

        verify(userDao).findByEmail(email);
        verify(newPasswordTokenDao).create(Mockito.any(NewPasswordTokenDto.class));
        verify(emailSenderService).sendHtmlMail(Mockito.any(MailBody.class));
    }

    @Test
    void loadUserByUsernameShouldReturnUserDetails() {
        // Arrange
        String email = "test@example.com";
        ApplicationUserDto user = new ApplicationUserDto();
        user.setEmail(email);
        user.setPassword("encodedPassword");
        user.setAdmin(true);

        when(userDao.findByEmail(email)).thenReturn(user);

        // Act
        UserDetails userDetails = userService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsernameShouldThrowUsernameNotFoundExceptionIfUserNotFound() {
        // Arrange
        String email = "abc@email.com";
        when(userDao.findByEmail(email)).thenReturn(null);

        // Act and Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(email);
        });
        Assertions.assertNotNull(exception);
    }

    @Test
    void loadUserByUsernameForNormalUserShouldSucceed() {
        ApplicationUserDto user = ApplicationUserSupplier.aCustomerUser();
        when(userDao.findByEmail(user.getEmail())).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        Assertions.assertNotNull(userDetails);
    }

    @Test
    void deleteUserShouldDeleteUser() throws Exception {
        ApplicationUserDto user = ApplicationUserSupplier.anAdminUser();
        user.setId(-123L);
        when(userDao.findById(user.getId())).thenReturn(user);

        userService.deleteUser(user.getId());

        verify(userDao).deleteById(user.getId());
    }

    @Test
    void deleteUserShouldThrowDtoNotFoundException() throws Exception {
        ApplicationUserDto user = ApplicationUserSupplier.anAdminUser();
        user.setId(-123L);
        when(userDao.findById(user.getId())).thenThrow(EntityNotFoundException.class);

        assertThrows(DtoNotFoundException.class, () -> userService.deleteUser(user.getId()));
    }

    @Test
    void deleteUserShouldThrowDtoNotFoundExceptionIfUserNotFound() throws Exception {
        ApplicationUserDto user = ApplicationUserSupplier.anAdminUser();
        user.setId(-123L);
        when(userDao.findById(user.getId())).thenReturn(user);
        doThrow(EntityNotFoundException.class).when(userDao).deleteById(user.getId());

        assertThrows(DtoNotFoundException.class, () -> userService.deleteUser(user.getId()));
    }

    @Test
    void activateAccountShouldSucceed() throws Exception {
        ApplicationUserDto userDto = ApplicationUserSupplier.anAdminUser();

        // Arrange
        String token = UUID.randomUUID().toString();
        AccountActivateTokenDto accountActivateToken = new AccountActivateTokenDto();
        accountActivateToken.setToken(token);
        accountActivateToken.setEmail("test@email.com");
        accountActivateToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        accountActivateToken.setId(1L);

        when(accountActivateTokenDao.findByToken(token)).thenReturn(accountActivateToken);
        when(userDao.findByEmail(accountActivateToken.getEmail())).thenReturn(userDto);
        when(userDao.update(userDto)).thenReturn(userDto);

        // Act
        userService.activateAccount(token);

        // Assert
        assertTrue(userDto.getAccountActivated());
        verify(userDao).update(userDto);
        verify(accountActivateTokenDao).deleteById(accountActivateToken.getId());
    }

    @Test
    void activateAccountShouldThrowValidationExceptionForExpiredToken() throws Exception {
        // Arrange
        String token = "expiredToken";
        AccountActivateTokenDto expiredToken = new AccountActivateTokenDto();
        expiredToken.setToken(token);
        expiredToken.setEmail("test@example.com");
        expiredToken.setExpiryDate(LocalDateTime.now().minusMinutes(10)); // Set expiry date to the past

        when(accountActivateTokenDao.findByToken(token)).thenReturn(expiredToken);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.activateAccount(token);
        });

        // Assert the exception message
        assertEquals("Dieser Link ist abgelaufen. Bitte neu registrieren.", exception.getMessage());

        // Verify interactions
        verify(accountActivateTokenDao).findByToken(token);
        verify(userDao, never()).findByEmail(anyString());
        verify(userDao, never()).update(any(ApplicationUserDto.class));
        verify(accountActivateTokenDao, never()).deleteById(anyLong());
    }

    @Test
    void activateAccountShouldThrowValidationExceptionForInvalidToken() throws Exception {
        // Arrange
        String token = "invalidToken";

        when(accountActivateTokenDao.findByToken(token)).thenReturn(null);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.activateAccount(token);
        });

        // Assert the exception message
        assertEquals("Dieser Link ist ungültig.", exception.getMessage());

        // Verify interactions
        verify(accountActivateTokenDao).findByToken(token);
        verify(userDao, never()).findByEmail(anyString());
        verify(userDao, never()).update(any(ApplicationUserDto.class));
        verify(accountActivateTokenDao, never()).deleteById(anyLong());
    }

    @Test
    void findApplicationUserByIdShouldReturnUserWhenIdIsValid() throws Exception {
        // Arrange
        Long userId = 1L;
        ApplicationUserDto userDto = ApplicationUserSupplier.anAdminUser();
        when(userDao.findById(userId)).thenReturn(userDto);

        // Act
        ApplicationUserDto result = userService.findApplicationUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userDto, result);
        verify(userDao).findById(userId);
    }

    @Test
    void findApplicationUserByIdShouldThrowDtoNotFoundExceptionWhenUserNotFound() throws Exception {
        // Arrange
        Long userId = 1L;
        when(userDao.findById(userId)).thenThrow(new EntityNotFoundException(userId));

        // Act & Assert
        DtoNotFoundException exception = assertThrows(DtoNotFoundException.class, () -> {
            userService.findApplicationUserById(userId);
        });

        assertEquals("Could not find the user with the id " + userId, exception.getMessage());
        verify(userDao).findById(userId);
    }

    @Test
    void findApplicationUserByIdShouldReturnNullWhenIdIsNull() throws Exception {
        // Act
        ApplicationUserDto result = userService.findApplicationUserById(null);

        // Assert
        assertNull(result);
        verify(userDao, never()).findById(null);
    }

    @Test
    void updatePasswordShouldThrowValidationExceptionWhenTokenIsInvalid() {
        // Arrange
        String token = "invalidToken";
        when(newPasswordTokenDao.findByToken(token)).thenReturn(null);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.updatePassword(token, "currentPassword", "newPassword");
        });

        assertEquals("Der Link ist ungültig.", exception.getMessage());
    }

    @Test
    void updatePasswordShouldThrowValidationExceptionWhenTokenIsExpired() {
        // Arrange
        String token = "expiredToken";
        NewPasswordTokenDto expiredToken = new NewPasswordTokenDto();
        expiredToken.setToken(token);
        expiredToken.setExpiryDate(LocalDateTime.now().minusMinutes(10));
        when(newPasswordTokenDao.findByToken(token)).thenReturn(expiredToken);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.updatePassword(token, "currentPassword", "newPassword");
        });

        assertEquals("Dieser Link ist abgelaufen.", exception.getMessage());
    }

    @Test
    void updatePasswordShouldThrowValidationExceptionWhenCurrentPasswordIsIncorrect() {
        // Arrange
        String token = "validToken";
        NewPasswordTokenDto validToken = new NewPasswordTokenDto();
        validToken.setToken(token);
        validToken.setEmail("test@email.com");
        validToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        ApplicationUserDto user = ApplicationUserSupplier.anAdminUser();
        user.setEmail(validToken.getEmail());
        user.setSalt("salt");
        user.setPassword("encodedOldPassword");

        when(newPasswordTokenDao.findByToken(token)).thenReturn(validToken);
        when(userDao.findByEmail(validToken.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("currentPassword" + user.getSalt(), user.getPassword())).thenReturn(false);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.updatePassword(token, "currentPassword", "newPassword");
        });

        assertEquals("Das alte Passwort ist falsch.", exception.getMessage());
    }

    @Test
    void updatePasswordShouldThrowValidationExceptionWhenNewPasswordIsSameAsCurrentPassword() {
        // Arrange
        String token = "validToken";
        NewPasswordTokenDto validToken = new NewPasswordTokenDto();
        validToken.setToken(token);
        validToken.setEmail("test@email.com");
        validToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        ApplicationUserDto user = ApplicationUserSupplier.anAdminUser();
        user.setEmail(validToken.getEmail());
        user.setSalt("salt");
        user.setPassword("encodedCurrentPassword");

        when(newPasswordTokenDao.findByToken(token)).thenReturn(validToken);
        when(userDao.findByEmail(validToken.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("currentPassword" + user.getSalt(), user.getPassword())).thenReturn(true);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.updatePassword(token, "currentPassword", "currentPassword");
        });

        assertEquals("Das neue Passwort muss sich vom alten Passwort unterscheiden.", exception.getMessage());
    }

    @Test
    void updatePasswordShouldThrowValidationExceptionWhenNewPasswordIsNullOrEmpty() {
        // Arrange
        String token = "validToken";
        NewPasswordTokenDto validToken = new NewPasswordTokenDto();
        validToken.setToken(token);
        validToken.setEmail("test@email.com");
        validToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        when(newPasswordTokenDao.findByToken(token)).thenReturn(validToken);

        // Act & Assert
        ValidationException exception1 = assertThrows(ValidationException.class, () -> {
            userService.updatePassword(token, null, null);
        });

        assertEquals("Das Passwort darf nicht leer sein.", exception1.getMessage());

        ValidationException exception2 = assertThrows(ValidationException.class, () -> {
            userService.updatePassword(token, null, "");
        });

        assertEquals("Das Passwort darf nicht leer sein.", exception2.getMessage());
    }

    @Test
    void updatePasswordShouldThrowValidationExceptionWhenNewPasswordIsTooShort() {
        // Arrange
        String token = "validToken";
        NewPasswordTokenDto validToken = new NewPasswordTokenDto();
        validToken.setToken(token);
        validToken.setEmail("test@email.com");
        validToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        ApplicationUserDto user = ApplicationUserSupplier.anAdminUser();
        user.setSalt("test");
        user.setPassword("encodedCurrentPassword"); // Set the encoded current password

        when(newPasswordTokenDao.findByToken(token)).thenReturn(validToken);
        when(userDao.findByEmail(validToken.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("currentPasswordtest", user.getPassword())).thenReturn(true); // Mock password match

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.updatePassword(token, "currentPassword", "short");
        });

        assertEquals("Das Passwort muss mindestens 8 Zeichen lang sein.", exception.getMessage());
    }

    @Test
    void updatePasswordShouldUpdatePasswordWhenAllConditionsAreMet() throws ValidationException, DtoNotFoundException, EntityNotFoundException {
        // Arrange
        String token = "validToken";
        String newPassword = "newValidPassword";
        NewPasswordTokenDto validToken = new NewPasswordTokenDto();
        validToken.setToken(token);
        validToken.setEmail("test@email.com");
        validToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        ApplicationUserDto user = ApplicationUserSupplier.anAdminUser();
        user.setEmail(validToken.getEmail());
        user.setSalt("salt");
        user.setPassword("encodedCurrentPassword");

        when(newPasswordTokenDao.findByToken(token)).thenReturn(validToken);
        when(userDao.findByEmail(validToken.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("currentPassword" + user.getSalt(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword + user.getSalt())).thenReturn("encodedNewPassword");

        // Act
        userService.updatePassword(token, "currentPassword", newPassword);

        // Assert
        verify(userDao).update(user);
        assertEquals("encodedNewPassword", user.getPassword());
    }


}