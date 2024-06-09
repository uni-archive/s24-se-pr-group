package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSeatDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.HallSectorDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.HallSpotDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan.HallplanSectionCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan.ShowHallplanSectionResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan.ShowHallplanSpotResponse;
import at.ac.tuwien.sepr.groupphase.backend.mapper.BaseEntityMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(uses = ShowHallSpotResponseMapper.class, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShowHallSectorResponseMapper extends BaseResponseMapper<HallSectorDto, ShowHallplanSectionResponse>, BaseEntityMapper<HallSector, HallSectorDto> {

    @Mapping(target = "spots", expression = "java(mapSpots(hallSectorDto))")
    @Mapping(target = "spotCount", expression = "java(countSpots(hallSectorDto))")
    ShowHallplanSectionResponse toResponse(HallSectorDto hallSectorDto);

    default int countSpots(HallSectorDto hallSectorDto) {
        return hallSectorDto.getSeats().size();
    }

    default List<ShowHallplanSpotResponse> mapSpots(HallSectorDto hallSectorDto) {
        var seats = hallSectorDto.getSeats();
        if (seats.isEmpty() || !(seats.getFirst() instanceof HallSeatDto)) {
            return List.of();
        }

        var seatMapper = Mappers.getMapper(ShowHallSpotResponseMapper.class);
        return seats.stream()
            .map(HallSeatDto.class::cast)
            .map(seatMapper::toResponse)
            .toList();
    }
}