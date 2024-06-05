package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EmailChangeTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.MailBody;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.EmailChangeTokenDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.UserDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.AddressService;
import at.ac.tuwien.sepr.groupphase.backend.service.EmailSenderService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<ApplicationUserDto> userDto;

    @Test
    void createUserShouldCallValidateAndSetSalt() throws ValidationException, ForbiddenException {
        ApplicationUserDto user = ApplicationUserSupplier.anAdminUser();
        doNothing().when(userValidator).validateForCreate(user);
        when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");
        when(addressService.create(user.getAddress())).thenReturn(user.getAddress());

        userService.createUser(user);

        verify(userValidator).validateForCreate(user);
        verify(userDao).create(userDto.capture());
        verify(addressService).create(user.getAddress());
        Assertions.assertNotNull(userDto.getValue().getSalt());
        Assertions.assertNotNull(userDto.getValue().getPassword());
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
        Assertions.assertEquals(mockUsers, result);
    }

    @Test
    void updateUserStatusByEmailShouldUpdateUser() throws NotFoundException, ValidationException, EntityNotFoundException {
        ApplicationUserDto userToUpdate = ApplicationUserSupplier.anAdminUser();
        userToUpdate.setEmail("update@email.com");
        String adminEmail = "admin@email.com";

        doNothing().when(userValidator).validateForUpdateStatus(userToUpdate, adminEmail);
        when(userDao.findByEmail(userToUpdate.getEmail())).thenReturn(userToUpdate);
        when(userDao.update(userToUpdate)).thenReturn(userToUpdate);

        ApplicationUserDto updatedUser = userService.updateUserStatusByEmail(userToUpdate, adminEmail);
    }

    @Test
    void updateUserStatusByEmailShouldThrowNotFoundExceptionIfUserDoesNotExist() throws ValidationException {
        ApplicationUserDto userToUpdate = ApplicationUserSupplier.anAdminUser();
        userToUpdate.setEmail("nonexistent@email.com");
        String adminEmail = "admin@email.com";

        doNothing().when(userValidator).validateForUpdateStatus(userToUpdate, adminEmail);
        when(userDao.findByEmail(userToUpdate.getEmail())).thenReturn(null);

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
            userService.updateUserStatusByEmail(userToUpdate, adminEmail);
        });

        verify(userValidator).validateForUpdateStatus(userToUpdate, adminEmail);
        verify(userDao).findByEmail(userToUpdate.getEmail());
        Assertions.assertEquals("Could not update the user with the email address nonexistent@email.com because it does not exist", exception.getMessage());
    }

    @Test
    void updateUserStatusByEmailShouldThrowNotFoundExceptionOnEntityNotFoundException() throws NotFoundException, ValidationException, EntityNotFoundException {
        ApplicationUserDto userToUpdate = ApplicationUserSupplier.anAdminUser();
        userToUpdate.setEmail("update@email.com");
        String adminEmail = "admin@email.com";

        doNothing().when(userValidator).validateForUpdateStatus(userToUpdate, adminEmail);
        when(userDao.findByEmail(userToUpdate.getEmail())).thenReturn(userToUpdate);
        when(userDao.update(userToUpdate)).thenThrow(new EntityNotFoundException(1L));

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
            userService.updateUserStatusByEmail(userToUpdate, adminEmail);
        });

        verify(userValidator).validateForUpdateStatus(userToUpdate, adminEmail);
        verify(userDao).findByEmail(userToUpdate.getEmail());
        verify(userDao).update(userToUpdate);
        Assertions.assertTrue(exception.getCause() instanceof EntityNotFoundException);
    }

    @Test
    void updateUserInfoShouldThrowValidationExceptionIfEmailAlreadyInUse() throws ValidationException, EntityNotFoundException {
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

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> {
            userService.updateUserInfo(userInfo);
        });

        Assertions.assertEquals("The new email address is already in use.", exception.getMessage());
    }

    @Test
    void updateUserInfoShouldThrowMailNotSentExceptionIfEmailSendingFails() throws EntityNotFoundException, MessagingException {
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

        MailNotSentException exception = Assertions.assertThrows(MailNotSentException.class, () -> {
            userService.updateUserInfo(userInfo);
        });

        Assertions.assertEquals("Error sending mail.", exception.getMessage());
    }

    @Test
    void updateUserInfoShouldThrowNotFoundExceptionIfUserDoesNotExist() throws EntityNotFoundException {
        ApplicationUserDto userInfo = new ApplicationUserDto();
        userInfo.setId(1L);
        userInfo.setEmail("new@email.com");

        when(userDao.findById(1L)).thenThrow(new EntityNotFoundException(1L));

        DtoNotFoundException exception = Assertions.assertThrows(DtoNotFoundException.class, () -> {
            userService.updateUserInfo(userInfo);
        });

        Assertions.assertEquals("Could not update the user because it does not exist.", exception.getMessage());
    }

    @Test
    void updateUserInfoShouldUpdatePhoneNumberWhenNotNull()
        throws ValidationException, MailNotSentException, EntityNotFoundException, MessagingException, DtoNotFoundException {
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
        Assertions.assertEquals("+436603333332", updatedUser.getPhoneNumber());

        // Verify no email was sent since email was not updated
        verify(emailSenderService, never()).sendHtmlMail(any(MailBody.class));
    }

    @Test
    void updateUserEmailWithValidTokenShouldUpdateEmailAndReturnUpdatedUser() throws EntityNotFoundException {
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
        Assertions.assertNotNull(result);
        Assertions.assertEquals("new@email.com", result.getEmail());
    }

    @Test
    void updateUserEmailWithValidTokenShouldReturnNullIfTokenIsNull() {
        // Arrange
        String token = null;

        // Act
        ApplicationUserDto result = userService.updateUserEmailWithValidToken(token);

        // Assert
        verify(emailChangeTokenDao, times(1)).findByToken(token);
        Assertions.assertNull(result);
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
        Assertions.assertNull(result);
    }

    @Test
    void updateUserEmailWithValidTokenShouldLogErrorAndReturnNullIfUserNotFound() throws EntityNotFoundException {
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
        Assertions.assertNull(result);
    }

    @Test
    void updateUserEmailWithValidTokenShouldInvalidateOldTokens() throws EntityNotFoundException {
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
        Assertions.assertNotNull(result);
        Assertions.assertEquals("new@email.com", result.getEmail());
    }


}