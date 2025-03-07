package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { EventMapper.class })
public interface ShowMapper extends BaseEntityMapper<Show, ShowDto> {

    @Mapping(source = "event", target = "event", qualifiedByName = "toEntityWithoutLazyProperties")
    ShowDto toDto(Show show,  @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    Show toEntity(ShowDto dto,  @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);
}
