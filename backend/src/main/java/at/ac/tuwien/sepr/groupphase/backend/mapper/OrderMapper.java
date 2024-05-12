package at.ac.tuwien.sepr.groupphase.backend.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Order;
import org.mapstruct.Mapper;

@Mapper
public interface OrderMapper extends BaseEntityMapper<Order, OrderDetailsDto> {
}