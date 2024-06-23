package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderSummaryDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {UserMapper.class, HallPlanMapper.class, TicketMapper.class, HallSpotMapper.class})
public interface OrderMapper extends BaseEntityMapper<Order, OrderDetailsDto> {

    @Mapping(target = "tickets", ignore = true)
    Order toEntityFromSummary(OrderSummaryDto dto, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    List<Order> toEntityFromSummary(List<OrderSummaryDto> dto, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(target = "ticketCount", ignore = true)
    @Mapping(target = "totalPriceNonReserved", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    @Named("toSummaryDto")
    OrderSummaryDto toSummaryDto(Order entity);

    List<OrderSummaryDto> toSummaryDto(List<Order> entity);

    @Override
    @Mapping(target = "tickets", ignore = true)
    OrderDetailsDto toDto(Order entity, CycleAvoidingMappingContext cycleAvoidingMappingContext);

    /*@Mapping(target = "tickets", expression = "java(funnyMap(entity.getTickets()))")
    @Na
    OrderDetailsDto toDtoBla(Order entity, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    default List<TicketDetailsDto> funnyMap(List<Ticket> tickets) {
        return tickets.stream().map(t -> {
            var mapper = Mappers.getMapper(TicketMapper.class);
            return mapper.toDto(t, new CycleAvoidingMappingContext());
        }).toList();
    }*/
}