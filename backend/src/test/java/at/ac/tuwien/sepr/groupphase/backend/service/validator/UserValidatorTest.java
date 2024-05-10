package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import static at.ac.tuwien.sepr.groupphase.backend.supplier.ApplicationUserSupplier.anAdminUser;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.UserDao;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    void createShouldThrowNothingForCorrectUser() throws ValidationException {
        userValidator.validateForCreate(anAdminUser());
    }

    @Test
    void validateForCreateShouldThrowExceptionIfEmailAlreadyExists() {
        ApplicationUserDto user = anAdminUser();
        Mockito.when(userDao.findByEmail(user.getEmail())).thenReturn(user);
        ValidationException validationException = Assertions.assertThrows(ValidationException.class,
            () -> userValidator.validateForCreate(user));
        Assertions.assertEquals("Validation Error: Email already in use", validationException.getMessage());
    }

    @Test
    void validateForCreateShouldThrowExceptionIfEmailAddressIsEmpty() {
        ApplicationUserDto user = anAdminUser().setEmail("");
        ValidationException validationException = Assertions.assertThrows(ValidationException.class,
            () -> userValidator.validateForCreate(user));
        Assertions.assertEquals("Validation Error: Email must not be empty", validationException.getMessage());
    }

    @Test
    void validateForCreateShouldThrowExceptionIfEmailAddressIsNull() {
        ApplicationUserDto user = anAdminUser().setEmail(null);
        ValidationException validationException = Assertions.assertThrows(ValidationException.class,
            () -> userValidator.validateForCreate(user));
        Assertions.assertEquals("Validation Error: Email must not be empty", validationException.getMessage());
    }

    @Test
    void validateForCreateShouldThrowExceptionIfEmailAddressIsNotValid() {
        ApplicationUserDto user = anAdminUser().setEmail("abc");
        ValidationException validationException = Assertions.assertThrows(ValidationException.class,
            () -> userValidator.validateForCreate(user));
        Assertions.assertEquals("Validation Error: Email must be valid", validationException.getMessage());
    }

    @Test
    void validateForCreateShouldThrowExceptionIfFirstNameIsEmpty() {
        ApplicationUserDto user = anAdminUser().setFirstName("");
        ValidationException validationException = Assertions.assertThrows(ValidationException.class,
            () -> userValidator.validateForCreate(user));
        Assertions.assertEquals("Validation Error: First name must not be empty", validationException.getMessage());
    }

    @Test
    void validateForCreateShouldThrowExceptionIfFamilyNameIsEmpty() {
        ApplicationUserDto user = anAdminUser().setFamilyName("");
        ValidationException validationException = Assertions.assertThrows(ValidationException.class,
            () -> userValidator.validateForCreate(user));
        Assertions.assertEquals("Validation Error: Family name must not be empty", validationException.getMessage());
    }

    @Test
    void validateForCreateShouldThrowExceptionIfPhoneNumberIsEmpty() {
        ApplicationUserDto user = anAdminUser().setPhoneNumber("");
        ValidationException validationException = Assertions.assertThrows(ValidationException.class,
            () -> userValidator.validateForCreate(user));
        Assertions.assertEquals("Validation Error: Phone number must not be empty", validationException.getMessage());
    }

    @Test
    void validateForCreateShouldThrowExceptionIfPhoneNumberIsNotValid() {
        ApplicationUserDto user = anAdminUser().setPhoneNumber("abc");
        ValidationException validationException = Assertions.assertThrows(ValidationException.class,
            () -> userValidator.validateForCreate(user));
        Assertions.assertEquals("Validation Error: Phone number must be valid", validationException.getMessage());

        ApplicationUserDto user2 = anAdminUser().setPhoneNumber("+4356");
        ValidationException validationException2 = Assertions.assertThrows(ValidationException.class,
            () -> userValidator.validateForCreate(user));
        Assertions.assertEquals("Validation Error: Phone number must be valid", validationException.getMessage());
    }
}