package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowResponse;
import org.mapstruct.Mapper;

@Mapper
public interface ShowResponseMapper extends BaseResponseMapper<ShowDto, ShowResponse> {
}
