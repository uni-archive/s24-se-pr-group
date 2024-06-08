package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSeatDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.HallSpotDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSeat;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassMapping;

@Mapper
public interface HallSpotMapper extends BaseEntityMapper<HallSpot, HallSpotDto> {

    @Override
    @SubclassMapping(source = HallSeatDto.class, target = HallSeat.class)
    HallSpot toEntity(HallSpotDto dto);

    @Override
    @SubclassMapping(source = HallSeat.class, target = HallSeatDto.class)
    @Mapping(target = "sector.hallPlan.sectors", ignore = true)
    @Mapping(target = "sector.seats", ignore = true)
    HallSpotDto toDto(HallSpot entity, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);
}
