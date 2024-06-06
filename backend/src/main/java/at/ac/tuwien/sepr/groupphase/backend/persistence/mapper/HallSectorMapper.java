package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSectorDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(uses = HallSpotMapper.class, componentModel = MappingConstants.ComponentModel.SPRING)
public interface HallSectorMapper extends BaseEntityMapper<HallSector, HallSectorDto> {

}
