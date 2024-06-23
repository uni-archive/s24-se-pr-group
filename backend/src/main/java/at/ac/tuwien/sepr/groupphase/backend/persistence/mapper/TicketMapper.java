package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = HallSpotMapper.class)
public interface TicketMapper extends BaseEntityMapper<Ticket, TicketDetailsDto> {

    @Override
    @Mapping(target = "hallSpot.sector.seats", ignore = true)
    @Mapping(target = "hallSpot.sector.hallPlan.sectors", ignore = true)
    @Mapping(target = "show.event.shows", ignore = true)
    @Mapping(target = "order.tickets", ignore = true)
    Ticket toEntity(TicketDetailsDto dto, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Override
    // @Mapping(target = "hallSpot.sector.seats", ignore = true)
    // @Mapping(target = "hallSpot.sector.hallPlan.sectors", ignore = true)
    @Mapping(target = "show.event.shows", ignore = true)
    // @Mapping(target = "order.invoices", ignore = true)
    TicketDetailsDto toDto(Ticket entity, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);
}
