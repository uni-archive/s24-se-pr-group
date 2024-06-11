package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface EventMapper extends BaseEntityMapper<Event, EventDto> {

    @Named("eventToDto")
    EventDto toDto(Event event);

    @Named("toEntityWithoutLazyProperties")
    @Mapping(target = "shows", ignore = true)
    EventDto toDto(Event entity);
}
