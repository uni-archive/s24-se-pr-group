package at.ac.tuwien.sepr.groupphase.backend.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSeatDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.HallSpotDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSeat;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

import java.util.List;

@Mapper(uses = HallSpotMapper.class)
public interface TicketMapper extends BaseEntityMapper<Ticket, TicketDetailsDto> {
   /* @Override
    @SubclassMapping(source = HallSeatDto.class, target = HallSpot.class)
    Ticket toEntity(TicketDetailsDto dto);

    @Override
    @SubclassMapping(source = HallSeat.class, target = HallSpotDto.class)
    TicketDetailsDto toDto(Ticket entity);*/
}
