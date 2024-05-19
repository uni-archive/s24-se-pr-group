package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderSummaryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDetailsResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderSummaryResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface OrderResponseMapper extends BaseResponseMapper<OrderDetailsDto, OrderDetailsResponse> {
    OrderSummaryDto toDtoFromSummary(OrderSummaryResponse dto);

    List<OrderSummaryDto> toDtoFromSummary(List<OrderSummaryResponse> dto);

    OrderSummaryResponse toSummaryResponse(OrderSummaryDto entity);

    List<OrderSummaryResponse> toSummaryResponse(List<OrderSummaryDto> entity);
}
