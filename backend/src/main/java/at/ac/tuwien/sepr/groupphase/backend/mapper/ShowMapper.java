package at.ac.tuwien.sepr.groupphase.backend.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowResponse;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = { EventMapper.class })
public interface ShowMapper extends BaseEntityMapper<Show, ShowDto>, BaseResponseMapper<ShowDto, ShowResponse> {
    @Mapping(source = "event.id", target = "eventid")
    ShowDto toDto(Show show,  @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(source = "eventid", target = "event")
    Show toEntity(ShowDto dto,  @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);
}
