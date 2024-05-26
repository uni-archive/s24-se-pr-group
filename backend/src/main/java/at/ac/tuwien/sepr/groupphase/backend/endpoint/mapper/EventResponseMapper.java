package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventResponse;
import org.mapstruct.Mapper;

@Mapper
public interface EventResponseMapper extends BaseResponseMapper<EventDto, EventResponse> {
}
