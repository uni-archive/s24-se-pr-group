package at.ac.tuwien.sepr.groupphase.backend.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSeatDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.HallSpotDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSeat;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

import java.util.List;

@Mapper
public interface HallSpotMapper extends BaseEntityMapper<HallSpot, HallSpotDto> {

    @Override
    @SubclassMapping(source = HallSeatDto.class, target = HallSeat.class)
    HallSpot toEntity(HallSpotDto dto);

    @Override
    @SubclassMapping(source = HallSeat.class, target = HallSeatDto.class)
    HallSpotDto toDto(HallSpot entity);
}
