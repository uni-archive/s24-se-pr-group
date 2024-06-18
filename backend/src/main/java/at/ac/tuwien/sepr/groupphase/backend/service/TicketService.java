package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.SectorTicketAddToOrderDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketAddToOrderDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;

import java.util.List;

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
     * Adds a ticket to an ongoing (non-purchased) order.
     *
     * @param ticket             The info for that ticket.
     * @param applicationUserDto The user who wants to add the ticket.
     * @return The created ticket.
     * @throws ValidationException if the request is invalid.
     * @throws ForbiddenException  if the user isn't authorized to add the ticket to that order.
     */
    TicketDetailsDto addTicketToOrder(TicketAddToOrderDto ticket, ApplicationUserDto applicationUserDto)
        throws ValidationException, ForbiddenException, DtoNotFoundException;

    /**
     * Adds a ticket to an ongoing (non-purchased) order.
     *
     * @param ticket             The info for that ticket.
     * @param applicationUserDto The user who wants to add the ticket.
     * @return The created ticket.
     * @throws ValidationException if the request is invalid.
     * @throws ForbiddenException  if the user isn't authorized to add the ticket to that order.
     */
    TicketDetailsDto addSectorTicketToOrder(SectorTicketAddToOrderDto ticket, ApplicationUserDto applicationUserDto)
        throws ValidationException, ForbiddenException, DtoNotFoundException;

    /**
     * Confirms payment on an order. This will cancel all Invalidation Jobs, and mark the tickets as valid if they are
     * bought, and reschedules the Invalidation job to 30 minutes before the show for reserved tickets.
     *
     * @param ticketDetailsDto the ticket to be confirmed
     */
    void confirmTicket(TicketDetailsDto ticketDetailsDto) throws SchedulerException, DtoNotFoundException;

    void changeTicketReserved(long ticketId, boolean setReserved, ApplicationUserDto userDto) throws ValidationException, ForbiddenException,
        DtoNotFoundException;

    void deleteTicket(long ticketId, ApplicationUserDto userDto) throws ValidationException, ForbiddenException;
    /**
     * Finds a ticket by its hash.
     *
     * @param ticketHash the hash of the ticket
     */
    TicketDetailsDto findByHash(String ticketHash) throws DtoNotFoundException;

    /**
     * Finds all tickets for a show.
     *
     * @param showId the id of the show
     */
    List<TicketDetailsDto> findForShowById(long showId);

    /**
     * Searches for tickets based on the given search criteria.
     *
     * @param ticketSearchDto The search criteria.
     * @return A page of tickets matching the search criteria.
     */
    Page<TicketDetailsDto> search(TicketSearchDto ticketSearchDto);

    /**
     * Validates a ticket.
     *
     * @param id the id of the ticket to validate.
     */
    void validateTicket(long id) throws DtoNotFoundException, ValidationException;
}
