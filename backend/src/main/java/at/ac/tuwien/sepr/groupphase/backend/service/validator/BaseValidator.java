package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;

import java.util.List;

public abstract class BaseValidator {
    protected void endValidation(List<String> errors) throws ValidationException {
        if (!errors.isEmpty()) {
            String message = "Validation Error: " + String.join(", ", errors);
            throw new ValidationException(message);
        }
    }
}
