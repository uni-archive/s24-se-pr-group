package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class TicketValidator extends AbstractValidator {

    public void validateForCancelReservation(TicketDetailsDto ticket) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (ticket == null) {
            errors.add("Ticket is null");
        }

        if (Objects.nonNull(ticket) && !ticket.isValid()) {
            errors.add("Cannot cancel invalid ticket.");
        }


        if (Objects.nonNull(ticket) && !ticket.isReserved()) {
            errors.add("Cannot cancel non-reserved ticket.");
        }

        endValidation(errors);
    }
}
