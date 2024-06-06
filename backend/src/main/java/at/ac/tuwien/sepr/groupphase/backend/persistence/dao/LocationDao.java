package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.LocationSearch;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.LocationMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AddressRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.specification.LocationSpecification;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class LocationDao extends AbstractDao<Location, LocationDto> {

    private final AddressRepository addressRepository;
    private final LocationSpecification specification;

    protected LocationDao(LocationRepository repository,
        LocationMapper mapper, AddressRepository addressRepository, LocationSpecification specification) {
        super(repository, mapper);
        this.addressRepository = addressRepository;
        this.specification = specification;
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

    public Page<LocationDto> search(LocationSearch locationSearch) {
        return ((LocationRepository) repository)
            .findAll(specification.getLocations(locationSearch), locationSearch.getPageable())
            .map(mapper::toDto);
    }
}
