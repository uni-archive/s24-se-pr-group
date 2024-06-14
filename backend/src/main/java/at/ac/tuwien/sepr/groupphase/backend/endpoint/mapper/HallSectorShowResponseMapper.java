package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSectorShowDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallSectorShowCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallSectorShowResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface HallSectorShowResponseMapper extends BaseResponseMapper<HallSectorShowDto, HallSectorShowResponse> {

    @Mapping(source = "sectorDto", target = "sector")
    HallSectorShowDto toDto(HallSectorShowCreationDto hallSectorShowCreationDto);

    List<HallSectorShowDto> toDtoListFromCreationDto(List<HallSectorShowCreationDto> hallSectorShowCreationDtoList);
}
