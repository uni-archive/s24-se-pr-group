package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallPlanDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan.HallplanCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.mapper.BaseEntityMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(uses = HallSectorCreateRequestMapper.class, componentModel = MappingConstants.ComponentModel.SPRING)
public interface HallPlanCreateRequestMapper extends BaseResponseMapper<HallPlanDto, HallplanCreateRequest>, BaseEntityMapper<HallPlan, HallPlanDto> {

    HallPlanDto toDto(HallplanCreateRequest hallplanCreateDto);
}