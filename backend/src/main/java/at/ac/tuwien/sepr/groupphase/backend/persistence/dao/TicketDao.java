package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.TicketMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.TicketRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class TicketDao extends AbstractDao<Ticket, TicketDetailsDto> {

    public TicketDao(TicketRepository repository, TicketMapper mapper) {
        super(repository, mapper);
    }

    @Transactional
    public TicketDetailsDto findById(long id) throws EntityNotFoundException {
        var opt = repository.findById(id);
        var found = opt.orElseThrow(() -> new EntityNotFoundException(id));
        return mapper.toDto(found);
    }

    @Transactional
    public List<TicketDetailsDto> findByUserId(long userId) {
        var tickets = ((TicketRepository) repository).findTicketsByUserId(userId);
        return mapper.toDto(tickets);
    }

    @Transactional
    public List<TicketDetailsDto> findForShowById(long userId) {
        var tickets = ((TicketRepository) repository).findTicketsByShowId(userId);
        return mapper.toDto(tickets);
    }

    @Transactional
    public void cancelReservedTicket(long id) {
        TicketRepository r = (TicketRepository) repository;
        r.cancelReservedTicket(id);
    }

    @Transactional
    public void invalidateAllTicketsForOrder(long orderId) {
        ((TicketRepository) repository).invalidateAllTicketsForOrder(orderId);
    }

    public boolean existsValidOrReservedTicketForShowAndSeat(long showId, long seatId) {
        return ((TicketRepository) repository).existsValidTicketForShowAndSeat(showId, seatId);
    }

    @Transactional
    public void setValidAllTicketsForOrder(long orderId) {
        ((TicketRepository) repository).setValidAllTicketsForOrder(orderId);
    }

    @Transactional
    public void changeTicketReserved(long ticketId, boolean setReserved) {
        ((TicketRepository) repository).changeTicketReserved(ticketId, setReserved);
    }
}
