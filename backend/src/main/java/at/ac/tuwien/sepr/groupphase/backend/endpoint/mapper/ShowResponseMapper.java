package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowListResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface ShowResponseMapper extends BaseResponseMapper<ShowDto, ShowResponse> {
    ShowDto toDto(ShowCreationDto showCreationDto);

    ShowListResponse toResponse(ShowListDto dto);
}
