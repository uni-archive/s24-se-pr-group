package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallPlanDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(uses = HallSectorMapper.class, componentModel = MappingConstants.ComponentModel.SPRING)
public interface HallPlanMapper extends BaseEntityMapper<HallPlan, HallPlanDto> {
}
