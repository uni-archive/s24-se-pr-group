package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;
import at.ac.tuwien.sepr.groupphase.backend.mapper.LocationMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AddressRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.LocationRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class LocationDao extends AbstractDao<Location, LocationDto> {

    private final AddressRepository addressRepository;

    protected LocationDao(LocationRepository repository,
        LocationMapper mapper, AddressRepository addressRepository) {
        super(repository, mapper);
        this.addressRepository = addressRepository;
    }

    @Override
    public LocationDto update(LocationDto dto) throws EntityNotFoundException {
        if (!repository.existsById(dto.getId())) {
            throw new EntityNotFoundException(dto.getId());
        }
        Optional<Address> byId = addressRepository.findById(dto.getAddress().getId());
        if (byId.isEmpty()) {
            throw new EntityNotFoundException(dto.getAddress().getId());
        }
        Location entity = mapper.toEntity(dto);
        entity.setAddress(byId.get());
        return mapper.toDto(repository.save(entity));
    }
}
