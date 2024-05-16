package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;

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
     * @return A list of tickets ({@link TicketDetailsDto}) belonging to the user.
     *         Returns an empty list if the user does not exist.
     */
    List<TicketDetailsDto> findForUserById(long userId);

    /**
     * A ticket is assigned for a seat which is in a (sector, show) tuple.
     * Additional information specific to this tuple (e.g. the price of the ticket, which depends on the sector as well as the given show) can be fetched with this method.
     *
     * @param ticket The ticket to load the additional information into.
     */
    void loadSectorShowForTicket(TicketDetailsDto ticket);
}
