package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.UserDao;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static at.ac.tuwien.sepr.groupphase.backend.supplier.ApplicationUserSupplier.anAdminUser;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserDao userDao;

    @Mock
    private AddressValidator addressValidator;

    @InjectMocks
    private UserValidator userValidator;

    private ApplicationUserDto validUserDto;

    @BeforeEach
    void setUp() {
        validUserDto = new ApplicationUserDto();
        validUserDto.setEmail("test@example.com");
        validUserDto.setPassword("validPassword");
        validUserDto.setFirstName("John");
        validUserDto.setFamilyName("Doe");
        validUserDto.setPhoneNumber("+436600000000");
        validUserDto.setAddress(new AddressDto());
    }

    @Test
    void validateForCreate_ShouldThrowException_WhenObjectIsNull() {
        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validateForCreate(null));
        assertEquals("Object must not be null", exception.getMessage());
    }

    @Test
    void validateForCreate_ShouldThrowException_WhenEmailIsAlreadyInUse() {
        when(userDao.findByEmail(validUserDto.getEmail())).thenReturn(anAdminUser());

        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validateForCreate(validUserDto));
        assertTrue(exception.getMessage().contains("Email already in use"));
    }

    @Test
    void validateForCreate_ShouldPass_WhenUserDtoIsValid() {
        when(userDao.findByEmail(validUserDto.getEmail())).thenReturn(null);
        when(addressValidator.validateAddress(validUserDto.getAddress())).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> userValidator.validateForCreate(validUserDto));
    }

    @Test
    void validateForUpdate_ShouldThrowException_WhenObjectIsNull() {
        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validateForUpdate(null));
        assertEquals("Keine Daten zum Aktualisieren", exception.getMessage());
    }

    @Test
    void validateForUpdate_ShouldThrowException_WhenIdIsNull() {
        validUserDto.setId(null);

        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validateForUpdate(validUserDto));
        assertTrue(exception.getMessage().contains("Keine ID zum Aktualisieren"));
    }

    @Test
    void validateForUpdate_ShouldPass_WhenUserDtoIsValid() {
        validUserDto.setId(1L);
        when(addressValidator.validateAddress(validUserDto.getAddress())).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> userValidator.validateForUpdate(validUserDto));
    }

    @Test
    void validateForUpdateStatus_ShouldThrowException_WhenEmailIsInvalid() {
        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validateForUpdateStatus("invalidEmail", "admin@example.com"));
        assertTrue(exception.getMessage().contains("Email ist ungültig"));
    }

    @Test
    void validateForUpdateStatus_ShouldThrowException_WhenAdminEmailIsInvalid() {
        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validateForUpdateStatus("user@example.com", "invalidEmail"));
        assertTrue(exception.getMessage().contains("Admin Email ist ungültig"));
    }

    @Test
    void validateForUpdateStatus_ShouldThrowException_WhenEmailsAreSame() {
        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validateForUpdateStatus("admin@example.com", "admin@example.com"));
        assertTrue(exception.getMessage().contains("Eigener Status kann nicht geändert werden"));
    }

    @Test
    void validateForUpdateStatus_ShouldPass_WhenEmailsAreValid() {
        assertDoesNotThrow(() -> userValidator.validateForUpdateStatus("user@example.com", "admin@example.com"));
    }

    @Test
    void validateForCreate_ShouldFail_WhenUserDtoIsNullForEveryField() {
        validUserDto = new ApplicationUserDto();
        validUserDto.setEmail(null);
        validUserDto.setPassword(null);
        validUserDto.setFirstName(null);
        validUserDto.setFamilyName(null);
        validUserDto.setPhoneNumber(null);
        validUserDto.setAddress(null);

        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validateForCreate(validUserDto));
        assertTrue(exception.getMessage().contains("Email ist leer"));
        assertTrue(exception.getMessage().contains("Passwort ist leer"));
        assertTrue(exception.getMessage().contains("Vorname ist leer"));
        assertTrue(exception.getMessage().contains("Familienname ist leer"));
        assertTrue(exception.getMessage().contains("Telefonnummer ist leer"));
    }

    @Test
    void validateForCreate_ShouldFail_WhenUserDtoIsEmptyForEveryField() {
        validUserDto = new ApplicationUserDto();
        validUserDto.setEmail("");
        validUserDto.setPassword("");
        validUserDto.setFirstName("");
        validUserDto.setFamilyName("");
        validUserDto.setPhoneNumber("");
        validUserDto.setAddress(null);

        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validateForCreate(validUserDto));
        assertTrue(exception.getMessage().contains("Email ist leer"));
        assertTrue(exception.getMessage().contains("Passwort ist leer"));
        assertTrue(exception.getMessage().contains("Vorname ist leer"));
        assertTrue(exception.getMessage().contains("Familienname ist leer"));
        assertTrue(exception.getMessage().contains("Telefonnummer ist leer"));
    }

    @Test
    void validateForCreate_ShouldFail_WhenUserDtoPasswordIsTooShort() {
        validUserDto.setPassword("1");
        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validateForCreate(validUserDto));
        assertTrue(exception.getMessage().contains("Passwort muss mindestens 8 Zeichen lang sein"));
    }

    @Test
    void validateForCreate_ShouldFail_WhenUserDtoEmailIsInvalid() {
        validUserDto.setEmail("invalidEmail");
        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validateForCreate(validUserDto));
        assertTrue(exception.getMessage().contains("Email ist ungültig"));
    }

    @Test
    void validateForCreate_ShouldFail_WhenUserDtoPhoneNumberIsInvalid() {
        validUserDto.setPhoneNumber("+43550");
        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validateForCreate(validUserDto));
        assertTrue(exception.getMessage().contains("Telefonnummer ist ungültig"));
    }

    @Test
    void validateForUpdateStatus_ShouldFail_WhenEmailIsEmpty() {
        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validateForUpdateStatus("", ""));
        assertTrue(exception.getMessage().contains("Email ist leer"));
    }

    @Test
    void validateForUpdateStatus_ShouldFail_WhenAdminEmailIsNullAndUserEmailIsNull() {
        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validateForUpdateStatus(null, null));
        assertTrue(exception.getMessage().contains("Email ist leer"));
        assertTrue(exception.getMessage().contains("Admin Email ist leer"));
    }

    @Test
    void validateForCreate_ShouldAddError_WhenPhoneNumberParsingThrowsException() {
        // Arrange
        String invalidPhoneNumber = "invalidPhoneNumber";
        validUserDto.setPhoneNumber(invalidPhoneNumber);
        when(userDao.findByEmail(validUserDto.getEmail())).thenReturn(null);
        when(addressValidator.validateAddress(validUserDto.getAddress())).thenReturn(new ArrayList<>());

        PhoneNumberUtil phoneNumberUtil = mock(PhoneNumberUtil.class);
        try {
            lenient().when(phoneNumberUtil.parse(invalidPhoneNumber, null)).thenThrow(new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, "Invalid number"));
        } catch (NumberParseException e) {
            // This will not happen in this context
        }

        // Act
        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validateForCreate(validUserDto));

        // Assert
        assertTrue(exception.getMessage().contains("Telefonnummer ist ungültig"));
    }
}
