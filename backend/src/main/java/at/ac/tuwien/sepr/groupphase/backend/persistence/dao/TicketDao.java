package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSpotDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.HallSpotMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.TicketMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.specification.TicketSpecification;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class TicketDao extends AbstractDao<Ticket, TicketDetailsDto> {

    private final TicketSpecification ticketSpecification;
    private final HallSpotMapper hallSpotMapper;

    public TicketDao(TicketRepository repository, TicketMapper mapper, TicketSpecification ticketSpecification, HallSpotMapper hallSpotMapper) {
        super(repository, mapper);
        this.ticketSpecification = ticketSpecification;
        this.hallSpotMapper = hallSpotMapper;
    }

    @Transactional
    public TicketDetailsDto findById(long id) throws EntityNotFoundException {
        var opt = repository.findById(id);
        var found = opt.orElseThrow(() -> new EntityNotFoundException(id));
        return mapper.toDto(found);
    }

    @Transactional
    public void validateTicketById(long id) {
        ((TicketRepository) repository).validateTicketById(id);
    }

    @Transactional
    public List<TicketDetailsDto> findByUserId(long userId) {
        var tickets = ((TicketRepository) repository).findTicketsByUserId(userId);
        return mapper.toDto(tickets);
    }

    @Transactional
    public List<TicketDetailsDto> findForShowById(long userId) {
        var tickets = ((TicketRepository) repository).findTicketsByShowId(userId);
        return ((TicketMapper) mapper).toDto(tickets);
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
    public void changeTicketReserved(long ticketId, boolean setReserved) {
        ((TicketRepository) repository).changeTicketReserved(ticketId, setReserved);
    }

    public TicketDetailsDto findByHash(String hash) throws EntityNotFoundException {
        return mapper.toDto(((TicketRepository) repository).findByHash(hash));
    }

    public List<HallSpotDto> findFreeSpotForSector(Long showId, Long sectorId) throws EntityNotFoundException {
        return hallSpotMapper.toDto(((TicketRepository) repository).findFreeSpotForSector(showId, sectorId));
    }

    @Transactional
    public Page<TicketDetailsDto> search(TicketSearchDto ticketSearchDto) {
        return ((TicketRepository) repository)
            .findAll(ticketSpecification.getTickets(ticketSearchDto), ticketSearchDto.pageable())
            .map(mapper::toDto);
    }
}
