package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.HallPlanDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.AddressDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.LocationDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.AddressService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.LocationValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    @Mock
    private LocationDao locationDao;

    @Mock
    private AddressService addressService;

    @Mock
    private LocationValidator locationValidator;

    @Mock
    private HallPlanServiceImpl hallPlanServiceImpl;

    @InjectMocks
    private LocationServiceImpl locationService;

    @Test
    void createShouldValidateLocationAndCreateAddress() throws ForbiddenException, ValidationException, DtoNotFoundException {
        // given
        LocationDto locationDto = new LocationDto();
        AddressDto addressDto = new AddressDto();
        locationDto.setAddress(addressDto);
        HallPlanDto hallPlan = new HallPlanDto();
        hallPlan.setId(1L);
        locationDto.setHallPlan(hallPlan);
        when(addressService.create(addressDto)).thenReturn(addressDto);
        when(locationDao.create(locationDto)).thenReturn(locationDto);
        when(hallPlanServiceImpl.findById(locationDto.getHallPlan().getId())).thenReturn(new HallPlanDto());
        // when
        LocationDto locationDto1 = locationService.createLocation(locationDto);
        // then
        assertEquals(locationDto, locationDto1);
        verify(locationValidator).validateForCreate(locationDto);
        verify(addressService).create(addressDto);
        verify(locationDao).create(locationDto);
    }

    @Test
    void updateShouldValidateLocationAndAddress() throws ValidationException, ForbiddenException, EntityNotFoundException, DtoNotFoundException {
        // given
        LocationDto locationDto = new LocationDto();
        AddressDto addressDto = new AddressDto();
        locationDto.setAddress(addressDto);
        when(addressService.update(addressDto)).thenReturn(addressDto);
        when(locationDao.update(locationDto)).thenReturn(locationDto);
        // when
        LocationDto update = locationService.update(locationDto);
        // then
        assertEquals(locationDto, update);
        verify(locationValidator).validateForUpdate(locationDto);
        verify(addressService).update(addressDto);
        verify(locationDao).update(locationDto);
    }
}