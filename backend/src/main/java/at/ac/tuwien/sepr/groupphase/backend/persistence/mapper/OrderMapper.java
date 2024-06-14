package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderSummaryDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Order;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = {UserMapper.class, HallPlanMapper.class, TicketMapper.class})
public interface OrderMapper extends BaseEntityMapper<Order, OrderDetailsDto> {

    @Mapping(target = "tickets", ignore = true)
    Order toEntityFromSummary(OrderSummaryDto dto, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    List<Order> toEntityFromSummary(List<OrderSummaryDto> dto, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(target = "ticketCount", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    @Named("toSummaryDto")
    OrderSummaryDto toSummaryDto(Order entity);

    List<OrderSummaryDto> toSummaryDto(List<Order> entity);
}