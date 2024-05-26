package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventValidator extends BaseValidator {
    public void validateForCreateEvent(EventCreationDto createDto) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (createDto.getDescription() == null || createDto.getDescription().isEmpty()) {
            errors.add("Descrition is null or Empty");
        }
        if (createDto.getEventType() == null) {
            errors.add("EvetType is null or Empty");
        }
        if (createDto.getDuration() == 0) {
            errors.add("Event has Duration 0");
        }
        if (createDto.getTitle() == null || createDto.getTitle().isEmpty()) {
            errors.add("Title is null or Empty");
        }

        endValidation(errors);
    }
}
