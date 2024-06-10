package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallPlanDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.HallSectorShowDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan.HallplanCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan.ShowHallplanResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan.ShowHallplanSectionResponse;
import at.ac.tuwien.sepr.groupphase.backend.mapper.BaseEntityMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;

@Mapper(uses = ShowHallSectorResponseMapper.class, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShowHallPlanResponseMapper extends BaseResponseMapper<HallPlanDto, ShowHallplanResponse>, BaseEntityMapper<HallPlan, HallPlanDto> {

    @Named("map with custom sectors")
    @Mapping(target = "sectors", expression = "java(mapSectors(dto, hallSectorShowList))")
    ShowHallplanResponse toResponse(HallPlanDto dto, @Context List<HallSectorShowDto> hallSectorShowList);

    default List<ShowHallplanSectionResponse> mapSectors(HallPlanDto dto, List<HallSectorShowDto> hallSectorShowList) {
        return dto.getSectors().stream()
            .map(sector -> {
                var hss = hallSectorShowList.stream().filter(hs -> Objects.equals(hs.getSector().getId(), sector.getId())).findFirst().get();
                var mapper = Mappers.getMapper(ShowHallSectorResponseMapper.class);
                return mapper.toResponse(sector, hss);
            })
            .toList();
    }
}