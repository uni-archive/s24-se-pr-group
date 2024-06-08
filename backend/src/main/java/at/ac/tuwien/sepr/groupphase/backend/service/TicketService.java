package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import java.util.List;
import org.quartz.SchedulerException;

public interface TicketService {

    /**
     * Gets the ticket with the given id.
     *
     * @param id the ticket-id.
     * @return {@link TicketDetailsDto}
     * @throws DtoNotFoundException if no ticket with this id exists.
     */
    TicketDetailsDto findById(long id) throws DtoNotFoundException;

    /**
     * Gets all tickets belonging to a given user.
     *
     * @param userId the id of the user.
     * @return A list of tickets ({@link TicketDetailsDto}) belonging to the user. Returns an empty list if the user
     * does not exist.
     */
    List<TicketDetailsDto> findForUserById(long userId);

    List<TicketDetailsDto> findForShowById(long showId);

    /**
     * A ticket is assigned for a seat which is in a (sector, show) tuple. Additional information specific to this tuple
     * (e.g. the price of the ticket, which depends on the sector as well as the given show) can be fetched with this
     * method.
     *
     * @param ticket The ticket to load the additional information into.
     */
    void loadSectorShowForTicket(TicketDetailsDto ticket);


    /**
     * Cancels a ticket if and only if it is reserved and valid.
     *
     * @param id the id of the ticket to cancel.
     * @throws ValidationException  when a ticket is not reserved or invalid.
     * @throws DtoNotFoundException when no ticket with the given id exists.
     */
    void cancelReservedTicket(long id) throws ValidationException, DtoNotFoundException;


    /**
     * Invalidates all tickets of an order. Does nothing if the order does not exist.
     *
     * @param orderId The id of the order.
     */
    void invalidateAllTicketsForOrder(long orderId);

    /**
     * Adds a ticket to an order.
     *
     * @param seatId
     * @param showId
     * @param orderId
     * @param reservationOnly if the ticket should be reserved only
     * @return the created ticket
     * @throws ValidationException
     */
    TicketDetailsDto addTicketToOrder(Long seatId, Long showId, Long orderId,
        boolean reservationOnly) throws ValidationException;

    /**
     * Confirms payment on an order. This will cancel all Invalidation Jobs, and mark the tickets as valid if they are
     * bought, and reschedules the Invalidation job to 30 minutes before the show for reserved tickets.
     *
     * @param ticketDetailsDto the ticket to be confirmed
     */
    void confirmTicket(TicketDetailsDto ticketDetailsDto) throws SchedulerException, DtoNotFoundException;
}
