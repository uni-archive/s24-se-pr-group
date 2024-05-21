package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDetailsResponse;
import org.mapstruct.Mapper;

@Mapper
public interface OrderDetailsResponseMapper extends BaseResponseMapper<OrderDetailsDto, OrderDetailsResponse> {
}
