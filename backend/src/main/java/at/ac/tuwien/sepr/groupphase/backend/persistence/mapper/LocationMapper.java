package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Location;
import org.mapstruct.MapMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {AddressMapper.class, HallPlanMapper.class}, componentModel = "spring")
public interface LocationMapper extends BaseEntityMapper<Location, LocationDto> {

}
