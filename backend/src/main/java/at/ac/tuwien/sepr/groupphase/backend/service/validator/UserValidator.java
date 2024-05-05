package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {


    public void validateForCreate(ApplicationUserDto objectToCreate) throws ValidationException {
        validateForUpdate(objectToCreate);
    }

    // TODO: Implement more thoroughly, this is just Copilot
    public void validateForUpdate(ApplicationUserDto objectToUpdate) throws ValidationException {
        if (objectToUpdate.getEmail() == null || objectToUpdate.getEmail().isEmpty()) {
            throw new ValidationException("Email must not be empty");
        }
        if (objectToUpdate.getPassword() == null || objectToUpdate.getPassword().isEmpty()) {
            throw new ValidationException("Password must not be empty");
        }
        if (objectToUpdate.getFirstName() == null || objectToUpdate.getFirstName().isEmpty()) {
            throw new ValidationException("First name must not be empty");
        }
        if (objectToUpdate.getFamilyName() == null || objectToUpdate.getFamilyName().isEmpty()) {
            throw new ValidationException("Family name must not be empty");
        }
        if (objectToUpdate.getPhoneNumber() == null || objectToUpdate.getPhoneNumber().isEmpty()) {
            throw new ValidationException("Phone number must not be empty");
        }
    }
}
