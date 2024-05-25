package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class ShowValidator extends AbstractValidator<ShowDto> {
    public void validateForCreate(ShowCreationDto creationDto) throws ValidationException {
        List<String> errors = new ArrayList<>();
        if (creationDto.getDateTime() == null) {
            errors.add("No DateTime set for Event.");
        }
        endValidation(errors);
    }
}
