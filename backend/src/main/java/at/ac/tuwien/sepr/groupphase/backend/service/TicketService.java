package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;

public interface TicketService {

    /**
     * Gets the ticket with the given id.
     *
     * @param id the ticket-id.
     * @return {@link TicketDetailsDto}
     * @throws DtoNotFoundException if no ticket with this id exists.
     */
    TicketDetailsDto findById(long id) throws DtoNotFoundException;
}
