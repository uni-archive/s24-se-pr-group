package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.HallSpotDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.OrderDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.ShowDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.TicketDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class TicketValidator extends AbstractValidator<TicketDetailsDto> {

    private final TicketDao ticketDao;
    private final ShowDao showDao;
    private final HallSpotDao hallSpotDao;
    private final OrderDao orderDao;

    public TicketValidator(TicketDao ticketDao, ShowDao showDao, HallSpotDao hallSpotDao, OrderDao orderDao) {
        this.ticketDao = ticketDao;
        this.showDao = showDao;
        this.hallSpotDao = hallSpotDao;
        this.orderDao = orderDao;
    }

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

    public void validateForCreate(Long showId, Long spotId, Long orderId) throws ValidationException {
        List<String> errors = new ArrayList<>();
        if (Objects.isNull(showId)) {
            errors.add("Show id is null");
        }
        try {
            showDao.findById(showId);
        } catch (EntityNotFoundException e) {
            errors.add("Show with id " + showId + " does not exist.");
        }
        if (Objects.isNull(spotId)) {
            errors.add("Spot id is null");
        }
        if (!hallSpotDao.existsById(spotId)) {
            errors.add("Spot with id " + spotId + " does not exist.");
        }
        if (Objects.isNull(orderId)) {
            errors.add("Order id is null");
        }
        try {
            orderDao.findById(orderId);
        } catch (EntityNotFoundException e) {
            errors.add("Order with id " + orderId + " does not exist.");
        }
        if (ticketDao.existsValidOrReservedTicketForShowAndSeat(showId, spotId)) {
            errors.add("Ticket for show with id " + showId + " and spot with id " + spotId + " already exists.");
        }
        endValidation(errors);
    }
}
