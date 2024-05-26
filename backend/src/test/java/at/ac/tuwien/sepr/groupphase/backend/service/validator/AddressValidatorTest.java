package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.AddressDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddressValidatorTest {

    @Mock
    private AddressDao addressDao;

    @InjectMocks
    private AddressValidator addressValidator;

    private AddressDto validAddressDto;
    private AddressDto invalidAddressDto;

    @BeforeEach
    void setUp() {
        validAddressDto = new AddressDto(1L, "123 Main St", "Vienna", "1010", "Austria");
        invalidAddressDto = new AddressDto(null, "", "", "not-a-number", "");
    }

    @Test
    void validateForCreateShouldNotThrowExceptionForValidAddress() {
        assertDoesNotThrow(() -> addressValidator.validateForCreate(validAddressDto));
    }

    @Test
    void validateForCreateShouldThrowValidationExceptionForInvalidAddress() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            addressValidator.validateForCreate(invalidAddressDto);
        });

        List<String> expectedErrors = List.of(
            "Street must not be empty",
            "City must not be empty",
            "Zip code must be a number");

        for (String error : expectedErrors) {
            assertTrue(exception.getMessage().contains(error));
        }
    }

    @Test
    void validateForCreateShouldThrowValidationExceptionForEmptyZipCode() {
        invalidAddressDto.setZip("");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            addressValidator.validateForCreate(invalidAddressDto);
        });

        assertTrue(exception.getMessage().contains("Zip code must not be empty"));
    }

    @Test
    void validateForUpdateShouldThrowValidationExceptionIfAddressDoesNotExist() throws EntityNotFoundException {
        when(addressDao.findById(anyLong())).thenThrow(new EntityNotFoundException(2L));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            addressValidator.validateForUpdate(validAddressDto);
        });

        assertTrue(exception.getMessage().contains("Address with id 1 does not exist"));
    }

    @Test
    void validateForUpdateShouldThrowValidationExceptionForInvalidAddress() throws EntityNotFoundException {
        doThrow(new EntityNotFoundException(2L)).when(addressDao).findById(anyLong());

        invalidAddressDto.setId(2L);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            addressValidator.validateForUpdate(invalidAddressDto);
        });

        List<String> expectedErrors = List.of(
            "Street must not be empty",
            "City must not be empty",
            "Zip code must be a number",
            "Address with id 2 does not exist"
        );

        for (String error : expectedErrors) {
            assertTrue(exception.getMessage().contains(error));
        }
    }
}
