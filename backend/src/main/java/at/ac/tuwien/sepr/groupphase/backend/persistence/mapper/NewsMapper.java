package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface NewsMapper extends BaseEntityMapper<News, NewsDto> {

    @Mapping(source = "event", target = "eventDto")
    @Named("newsToDto")
    NewsDto toDto(News news);

    @Mapping(source = "eventDto", target = "event")
    @Named("dtoToNews")
    News toEntity(NewsDto newsDto);

    @Mapping(source = "event", target = "eventDto")
    @Named("newsToDtoWithContext")
    NewsDto toDtoWithContext(News entity, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "eventDto", target = "event")
    @Named("dtoToNewsWithContext")
    News toEntityWithContext(NewsDto dto, @Context CycleAvoidingMappingContext context);
}
