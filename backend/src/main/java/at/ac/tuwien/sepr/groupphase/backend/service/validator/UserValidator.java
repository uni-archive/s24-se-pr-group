package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.UserDao;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

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

    private void endValidation(List<String> errors) throws ValidationException {
        if (!errors.isEmpty()) {
            String message = "Validation Error: " + String.join(", ", errors);
            throw new ValidationException(message);
        }
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

    public void validateForUpdate(ApplicationUserDto objectToUpdate, ApplicationUserDto oldObject)
        throws ValidationException {
        List<String> errors = validateBaseCases(objectToUpdate);
        if (!objectToUpdate.getEmail().equals(oldObject.getEmail())) {
            if (Objects.nonNull(objectToUpdate.getEmail()) && userDao.findByEmail(objectToUpdate.getEmail()) != null) {
                errors.add("Email already in use");
            }
        }
        endValidation(errors);
    }
}
