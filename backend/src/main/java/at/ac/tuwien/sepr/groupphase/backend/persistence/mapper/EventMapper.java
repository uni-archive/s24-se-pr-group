package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = {ReferenceMapper.class})
public interface EventMapper extends BaseEntityMapper<Event, EventDto> {
    Event toEntity(Long id);

    @Named("toEntityWithoutLazyProperties")
    @Mapping(target = "shows", ignore = true)
    EventDto toDto(Event entity);
}
