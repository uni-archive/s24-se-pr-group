package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDetailsResponse;
import org.mapstruct.Mapper;

@Mapper(uses = HallSpotResponseMapper.class)
public interface TicketResponseMapper extends BaseResponseMapper<TicketDetailsDto, TicketDetailsResponse> {
}
