package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.TicketDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.HallSectorShowService;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TicketDao ticketDao;

    private final HallSectorShowService hallSectorShowService;

    public TicketServiceImpl(TicketDao ticketDao, HallSectorShowService hallSectorShowService) {
        this.ticketDao = ticketDao;
        this.hallSectorShowService = hallSectorShowService;
    }

    @Override
    public TicketDetailsDto findById(long id) throws DtoNotFoundException {
        try {
            var ticket = ticketDao.findById(id);
            loadSectorShowForTicket(ticket);
            return ticket;
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }
    }

    @Override
    public List<TicketDetailsDto> findForUserById(long userId) {
        var tickets = ticketDao.findByUserId(userId);
        tickets.forEach(this::loadSectorShowForTicket);
        return tickets;
    }

    public void loadSectorShowForTicket(TicketDetailsDto ticket) {
        try {
            // handle cyclic dependencies between sectors and shows that apply to each ticket
            var sectorShow = hallSectorShowService.findByShowIdAndHallSectorId(
                ticket.getShow().getId(),
                ticket.getHallSpot().getSector().getId()
            );
            ticket.getHallSpot().getSector().setHallSectorShow(sectorShow);
        } catch (EntityNotFoundException ex) {
            // invalid state
            throw new IllegalStateException("A ticket must be assigned to a hall-sector-show.");
        }
    }
}
