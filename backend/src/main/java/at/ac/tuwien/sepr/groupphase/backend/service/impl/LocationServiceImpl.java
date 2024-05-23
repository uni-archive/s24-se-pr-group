package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.LocationSearch;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.LocationDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.AbstractService;
import at.ac.tuwien.sepr.groupphase.backend.service.AddressService;
import at.ac.tuwien.sepr.groupphase.backend.service.LocationService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.LocationValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LocationServiceImpl extends AbstractService<LocationDto> implements
    LocationService {

    private final AddressService addressService;
    private final LocationValidator locationValidator;
    private final LocationDao locationDao;

    public LocationServiceImpl(
        LocationValidator locationValidator, LocationDao locationDao, AddressService addressService) {
        super(locationValidator, locationDao);
        this.addressService = addressService;
        this.locationValidator = locationValidator;
        this.locationDao = locationDao;
    }

    @Override
    public LocationDto create(LocationDto dto) throws ValidationException, ForbiddenException {
        locationValidator.validateForCreate(dto);
        AddressDto addressDto = addressService.create(dto.getAddress());
        dto.setAddress(addressDto);
        return locationDao.create(dto);
    }

    @Override
    public Page<LocationDto> search(LocationSearch locationSearchRequest) {
        if (Objects.isNull(locationSearchRequest.getPageable())) {
            PageRequest defaultPage = PageRequest.of(0, 15, Sort.by("name"));
            locationSearchRequest.setPageable(defaultPage);
        }
        return locationDao.search(locationSearchRequest);
    }

    @Override
    public LocationDto update(LocationDto dto) throws ValidationException, EntityNotFoundException, ForbiddenException {
        locationValidator.validateForUpdate(dto);
        AddressDto addressDto = addressService.update(dto.getAddress());
        dto.setAddress(addressDto);
        return locationDao.update(dto);
    }
}
