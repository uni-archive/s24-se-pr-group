package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Address;
import org.mapstruct.Mapper;

@Mapper
public interface AddressMapper extends BaseEntityMapper<Address, AddressDto> {

}
