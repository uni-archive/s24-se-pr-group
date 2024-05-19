package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.mapper.TicketMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.TicketRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class TicketDao extends AbstractDao<Ticket, TicketDetailsDto> {

    public TicketDao(TicketRepository repository, TicketMapper mapper) {
        super(repository, mapper);
    }

    public TicketDetailsDto findById(long id) throws EntityNotFoundException {
        var opt = repository.findById(id);
        var found = opt.orElseThrow(() -> new EntityNotFoundException(id));
        return mapper.toDto(found);
    }

    public List<TicketDetailsDto> findByUserId(long userId) {
        var tickets = ((TicketRepository) repository).findTicketsByUserId(userId);
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
}
