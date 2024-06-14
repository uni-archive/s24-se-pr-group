package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSectorShowDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSectorShow;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface HallSectorShowMapper extends BaseEntityMapper<HallSectorShow, HallSectorShowDto> {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "price", target = "price")
    @Override
    HallSectorShowDto toDto(HallSectorShow hallSectorShow, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

}
