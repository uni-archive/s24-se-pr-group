package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import org.mapstruct.Mapper;

@Mapper(uses = HallSpotMapper.class)
public interface TicketMapper extends BaseEntityMapper<Ticket, TicketDetailsDto> {
}
