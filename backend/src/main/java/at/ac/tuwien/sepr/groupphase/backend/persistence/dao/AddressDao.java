package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.BaseEntityMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AddressRepository;
import org.springframework.stereotype.Component;

@Component
public class AddressDao extends AbstractDao<Address, AddressDto> {

    protected AddressDao(AddressRepository repository,
        BaseEntityMapper<Address, AddressDto> mapper) {
        super(repository, mapper);
    }
}
