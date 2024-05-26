package at.ac.tuwien.sepr.groupphase.backend.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Location;
import org.mapstruct.Mapper;

@Mapper(uses = {AddressMapper.class}, componentModel = "spring")
public interface LocationMapper extends BaseEntityMapper<Location, LocationDto> {

}
