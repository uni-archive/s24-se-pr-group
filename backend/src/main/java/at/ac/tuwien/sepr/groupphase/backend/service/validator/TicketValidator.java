package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketAddToOrderDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.HallSpotDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.OrderDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.ShowDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.TicketDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public void validateForCreate(TicketAddToOrderDto ticket) throws ValidationException {
        List<String> errors = new ArrayList<>();
        if (Objects.isNull(ticket.showId())) {
            errors.add("Show id is null");
        }
        try {
            showDao.findById(ticket.showId());
        } catch (EntityNotFoundException e) {
            errors.add("Show with id " + ticket.showId() + " does not exist.");
        }
        if (Objects.isNull(ticket.spotId())) {
            errors.add("Spot id is null");
        }
        if (!hallSpotDao.existsById(ticket.spotId())) {
            errors.add("Spot with id " + ticket.spotId() + " does not exist.");
        }
        if (Objects.isNull(ticket.orderId())) {
            errors.add("Order id is null");
        }
        try {
            var order = orderDao.findById(ticket.orderId());

            if (order.getPurchaseInvoice().isPresent()) {
                errors.add("Order with id %d is already purchased.".formatted(order.getId()));
            } else if (order.getCancellationInvoice().isPresent()) {
                errors.add("Order with id %d is already cancelled.".formatted(order.getId()));
            }

        } catch (EntityNotFoundException e) {
            errors.add("Order with id " + ticket.orderId() + " does not exist.");
        }

        if (ticketDao.existsValidOrReservedTicketForShowAndSeat(ticket.showId(), ticket.spotId())) {
            errors.add("Ticket for show with id " + ticket.showId() + " and spot with id " + ticket.spotId() + " already exists.");
        }
        endValidation(errors);
    }

    public void validateForDelete(long ticketId, OrderDetailsDto orderOfTicket) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (orderOfTicket.getTickets().stream().noneMatch(t -> t.getId().equals(ticketId))) {
            errors.add("Ticket to delete (id: %d) does not exist in the provided order (id: %d).".formatted(ticketId, orderOfTicket.getId()));
        } else if (orderOfTicket.getPurchaseInvoice().isPresent()) {
            errors.add("Cannot delete ticket from purchased order.");
        } else if (orderOfTicket.getCancellationInvoice().isPresent()) {
            errors.add("Cannot delete ticket from cancelled order.");
        }

        endValidation(errors);
    }

    public void validateForChangeTicketReserved(long ticketId, OrderDetailsDto orderOfTicket) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (orderOfTicket.getTickets().stream().noneMatch(t -> t.getId().equals(ticketId))) {
            errors.add("Ticket to delete (id: %d) does not exist in the provided order (id: %d).".formatted(ticketId, orderOfTicket.getId()));
        } else if (orderOfTicket.getPurchaseInvoice().isPresent()) {
            errors.add("Cannot delete ticket from purchased order.");
        } else if (orderOfTicket.getCancellationInvoice().isPresent()) {
            errors.add("Cannot delete ticket from cancelled order.");
        }

        endValidation(errors);
    }
}
