package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class ShowValidator extends BaseValidator {
    public void validateForCreate(ShowCreationDto creationDto) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (creationDto.getDateTime() == null) {
            errors.add("No DateTime set for Event.");
        }

        endValidation(errors);
    }
}
