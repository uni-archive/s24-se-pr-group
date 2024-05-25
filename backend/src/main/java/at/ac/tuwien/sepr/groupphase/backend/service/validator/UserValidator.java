package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.UserDao;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class UserValidator extends BaseValidator {

    protected static final String EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private final UserDao userDao;

    public UserValidator(UserDao userDao) {
        this.userDao = userDao;
    }


    public void validateForCreate(ApplicationUserDto objectToCreate) throws ValidationException {
        List<String> errors = validateBaseCases(objectToCreate);
        if (Objects.nonNull(objectToCreate.getEmail()) && userDao.findByEmail(objectToCreate.getEmail()) != null) {
            errors.add("Email already in use");
        }
        endValidation(errors);
    }


    private List<String> validateBaseCases(ApplicationUserDto userDto) {
        List<String> errors = new ArrayList<>();
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            errors.add("Email must not be empty");
        } else {
            if (!userDto.getEmail().matches(EMAIL_REGEX)) {
                errors.add("Email must be valid");
            }
        }
        if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            errors.add("Password must not be empty");
        } else {
            if (userDto.getPassword().length() < 8) {
                errors.add("Password must be at least 8 characters long");
            }
        }
        if (userDto.getFirstName() == null || userDto.getFirstName().isEmpty()) {
            errors.add("First name must not be empty");
        }
        if (userDto.getFamilyName() == null || userDto.getFamilyName().isEmpty()) {
            errors.add("Family name must not be empty");
        }
        if (userDto.getPhoneNumber() == null || userDto.getPhoneNumber().isEmpty()) {
            errors.add("Phone number must not be empty");
        } else {
            PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
            try {
                PhoneNumber phoneNumber = numberUtil.parse(userDto.getPhoneNumber(), null);
                if (!numberUtil.isValidNumber(phoneNumber)) {
                    errors.add("Phone number must be valid");
                }
            } catch (NumberParseException e) {
                errors.add("Phone number must be valid");
            }
        }
        return errors;
    }

    public void validateForUpdate(ApplicationUserDto objectToUpdate)
        throws ValidationException {
        List<String> errors = validateBaseCases(objectToUpdate);
        endValidation(errors);
    }

    public void validateForUpdateStatus(ApplicationUserDto objectToUpdate, String adminEmail) throws ValidationException {
        List<String> errors = new ArrayList<>();
        if (objectToUpdate == null) {
            errors.add("User must not be null");
            endValidation(errors);
        }
        if (objectToUpdate.getEmail() == null || objectToUpdate.getEmail().isEmpty()) {
            errors.add("Email must not be empty");
        } else {
            if (!objectToUpdate.getEmail().matches(EMAIL_REGEX)) {
                errors.add("Email must be valid");
            }
            if (objectToUpdate.getEmail().equals(adminEmail)) {
                errors.add("Cannot update own status");
            }
        }
        endValidation(errors);
    }
}
