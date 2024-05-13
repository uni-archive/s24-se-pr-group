package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSeatDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.HallSpotDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallSeatResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallSpotResponse;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper
public interface HallSpotResponseMapper extends BaseResponseMapper<HallSpotDto, HallSpotResponse> {
    @Override
    @SubclassMapping(source = HallSeatResponse.class, target = HallSeatDto.class)
    HallSpotDto toDto(HallSpotResponse response);

    @Override
    @SubclassMapping(source = HallSeatDto.class, target = HallSeatResponse.class)
    HallSpotResponse toResponse(HallSpotDto dto);
}
