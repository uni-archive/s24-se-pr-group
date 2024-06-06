package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.BaseEntityMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", implementationName = "backendNewsMapperImpl")
public interface NewsMapper extends BaseEntityMapper<News, NewsDto> {

    @Named("newsToDto")
    NewsDto toDto(News news);

    @Named("dtoToNews")
    News toEntity(NewsDto newsDto);

    @Named("newsToDtoWithContext")
    NewsDto toDto(News entity, @Context CycleAvoidingMappingContext context);

    @Named("dtoToNewsWithContext")
    News toEntity(NewsDto dto, @Context CycleAvoidingMappingContext context);
}

