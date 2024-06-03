package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSeatDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.HallSectorDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.HallSpotDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan.HallplanSectionCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.BaseEntityMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.stream.Stream;

@Mapper(uses = HallSeatCreateRequestMapper.class, componentModel = MappingConstants.ComponentModel.SPRING)
public interface HallSectorCreateRequestMapper {

    @Mapping(target = "seats", expression = "java(mapSpots(hallplanCreateDto))")
    HallSectorDto toDto(HallplanSectionCreateRequest hallplanCreateDto);

    default List<HallSpotDto> mapSpots(HallplanSectionCreateRequest hallSectorCreateRequest) {
        if (hallSectorCreateRequest.getStandingOnly()) {
            // map spots for standingOnly case
            return Stream.generate(HallSpotDto::new)
                    .limit(hallSectorCreateRequest.getSpotCount())
                    .toList();
        } else {
            // map spots for non-standingOnly case
            return hallSectorCreateRequest.getSpots().stream()
                    .map(hallSpotCreateRequest -> {
                        HallSeatDto dto = new HallSeatDto();
                        dto.setFrontendCoordinates(hallSpotCreateRequest.getFrontendCoordinates());
                        return (HallSpotDto) dto;
                    })
                    .toList();
        }
    }
}