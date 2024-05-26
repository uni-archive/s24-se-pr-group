package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSeatDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan.HallplanSeatCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.mapper.BaseEntityMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSeat;
import org.mapstruct.Mapper;

@Mapper
public interface HallSeatCreateRequestMapper extends BaseResponseMapper<HallSeatDto, HallplanSeatCreateRequest>, BaseEntityMapper<HallSeat, HallSeatDto> {

    HallSeatDto toDto(HallplanSeatCreateRequest hallplanCreateDto);
}