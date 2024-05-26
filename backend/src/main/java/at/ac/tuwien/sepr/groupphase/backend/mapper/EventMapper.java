package at.ac.tuwien.sepr.groupphase.backend.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import org.mapstruct.Mapper;

@Mapper(uses = {ReferenceMapper.class})
public interface EventMapper extends BaseEntityMapper<Event, EventDto>, BaseResponseMapper<EventDto, EventCreationDto> {
    Event toEntity(Long id);

}
