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
public class UserValidator extends AbstractValidator<ApplicationUserDto> {

    protected static final String EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final int MIN_PASSWORD_LENGTH = 8;
    private final UserDao userDao;
    private final AddressValidator addressValidator;

    public UserValidator(UserDao userDao, AddressValidator addressValidator) {
        this.userDao = userDao;
        this.addressValidator = addressValidator;
    }

    @Override
    public void validateForCreate(ApplicationUserDto objectToCreate) throws ValidationException {
        if (Objects.isNull(objectToCreate)) {
            throw new ValidationException("Object must not be null");
        }
        List<String> errors = validateBaseCases(objectToCreate);
        if (Objects.nonNull(objectToCreate.getEmail()) && userDao.findByEmail(objectToCreate.getEmail()) != null) {
            if (userDao.findByEmail(objectToCreate.getEmail()).getAccountActivated()) {
                errors.add("Email existiert bereits");
            }
        }
        endValidation(errors);
    }

    @Override
    public void validateForUpdate(ApplicationUserDto objectToUpdate) throws ValidationException {
        if (Objects.isNull(objectToUpdate)) {
            throw new ValidationException("Keine Daten zum Aktualisieren");
        }
        List<String> errors = validateBaseCases(objectToUpdate);
        if (Objects.isNull(objectToUpdate.getId())) {
            errors.add("Keine ID zum Aktualisieren");
        }
        endValidation(errors);
    }

    public void validateForUpdateStatus(String email, String adminEmail) throws ValidationException {
        List<String> errors = new ArrayList<>();
        if (email == null || email.isEmpty()) {
            errors.add("Email ist leer");
        } else {
            if (!email.matches(EMAIL_REGEX)) {
                errors.add("Email ist ungültig");
            }
        }
        if (adminEmail == null || adminEmail.isEmpty()) {
            errors.add("Admin Email ist leer");
        } else {
            if (!adminEmail.matches(EMAIL_REGEX)) {
                errors.add("Admin Email ist ungültig");
            }
        }
        endValidation(errors);
        if (email.equals(adminEmail)) {
            errors.add("Eigener Status kann nicht geändert werden");
        }
        endValidation(errors);
    }

    private List<String> validateBaseCases(ApplicationUserDto userDto) {
        List<String> errors = new ArrayList<>();
        validateEmail(userDto.getEmail(), errors);
        validatePassword(userDto.getPassword(), errors);
        validateFirstName(userDto.getFirstName(), errors);
        validateFamilyName(userDto.getFamilyName(), errors);
        validatePhoneNumber(userDto.getPhoneNumber(), errors);
        if (userDto.getAddress() != null) {
            errors.addAll(addressValidator.validateAddress(userDto.getAddress()));
        }
        return errors;
    }

    private void validateEmail(String email, List<String> errors) {
        if (email == null || email.isEmpty()) {
            errors.add("Email ist leer");
        } else {
            if (!email.matches(EMAIL_REGEX)) {
                errors.add("Email ist ungültig");
            }
            if (email.length() > 255) {
                errors.add("Email ist zu lang");
            }
        }
    }

    private void validatePassword(String password, List<String> errors) {
        if (password == null || password.isEmpty()) {
            errors.add("Passwort ist leer");
        } else {
            if (password.length() < MIN_PASSWORD_LENGTH) {
                errors.add("Passwort muss mindestens " + MIN_PASSWORD_LENGTH + " Zeichen lang sein");
            }
            if (password.length() > 255) {
                errors.add("Passwort ist zu lang");
            }
        }
    }

    private void validateFirstName(String firstName, List<String> errors) {
        if (firstName == null || firstName.isEmpty()) {
            errors.add("Vorname ist leer");
        } else {
            if (containsNumbers(firstName)) {
                errors.add("Vorname darf keine Zahlen enthalten");
            }
            if (firstName.length() > 255) {
                errors.add("Vorname ist zu lang");
            }
        }
    }

    private void validateFamilyName(String familyName, List<String> errors) {
        if (familyName == null || familyName.isEmpty()) {
            errors.add("Familienname ist leer");
        } else {
            if (containsNumbers(familyName)) {
                errors.add("Familienname darf keine Zahlen enthalten");
            }
            if (familyName.length() > 255) {
                errors.add("Familienname ist zu lang");
            }
        }
    }

    private void validatePhoneNumber(String phoneNumber, List<String> errors) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            errors.add("Telefonnummer ist leer");
        } else {
            PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
            try {
                PhoneNumber number = numberUtil.parse(phoneNumber, null);
                if (!numberUtil.isValidNumber(number)) {
                    errors.add("Telefonnummer ist ungültig");
                }
                if (phoneNumber.startsWith("++")) {
                    errors.add("Telefonnummer ist ungültig");
                }
            } catch (NumberParseException e) {
                errors.add("Telefonnummer ist ungültig");
            }
            if (phoneNumber.length() > 255) {
                errors.add("Telefonnummer ist zu lang");
            }
        }
    }

    private boolean containsNumbers(String name) {
        return name.matches(".*\\d.*");
    }

}
