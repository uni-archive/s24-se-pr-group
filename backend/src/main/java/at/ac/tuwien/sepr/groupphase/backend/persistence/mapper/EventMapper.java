package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import org.mapstruct.Mapper;

@Mapper(uses = {ReferenceMapper.class})
public interface EventMapper extends BaseEntityMapper<Event, EventDto> {
    Event toEntity(Long id);

}
