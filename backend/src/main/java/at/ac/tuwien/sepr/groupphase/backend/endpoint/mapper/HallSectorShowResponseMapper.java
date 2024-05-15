package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSectorShowDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallSectorShowResponse;
import org.mapstruct.Mapper;

@Mapper
public interface HallSectorShowResponseMapper extends BaseResponseMapper<HallSectorShowDto, HallSectorShowResponse> {
}
