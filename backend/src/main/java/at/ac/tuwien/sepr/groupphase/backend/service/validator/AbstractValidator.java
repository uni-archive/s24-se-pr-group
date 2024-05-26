package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.AbstractDto;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import java.util.List;
import java.util.Objects;

public abstract class AbstractValidator<T extends AbstractDto> {

    protected void endValidation(List<String> errors) throws ValidationException {
        if (!errors.isEmpty()) {
            String message = "Validation Error: " + String.join(", ", errors);
            throw new ValidationException(message);
        }
    }

    public void validateForUpdate(T object) throws ValidationException {
        if (Objects.isNull(object)) {
            throw new ValidationException("Object must not be null");
        }
        if (Objects.isNull(object.getId())) {
            throw new ValidationException("Id must not be null");
        }
    }

    public void validateForCreate(T object) throws ValidationException {
        if (Objects.isNull(object)) {
            throw new ValidationException("Object must not be null");
        }
    }
}
