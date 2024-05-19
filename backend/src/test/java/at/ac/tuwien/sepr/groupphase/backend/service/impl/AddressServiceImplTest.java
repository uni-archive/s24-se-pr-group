package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.AddressDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.UserDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.AddressValidator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.util.Authority.Code;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressDao addressDao;

    @Mock
    private AddressValidator addressValidator;

    @Mock
    private UserDao userDao;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void createShouldCallValidatorAndDao() throws ValidationException, ForbiddenException {
        AddressDto addressDto = new AddressDto();
        when(addressDao.create(addressDto)).thenReturn(addressDto);

        AddressDto result = addressService.create(addressDto);

        verify(addressValidator).validateForCreate(addressDto);
        verify(addressDao).create(addressDto);
        assertEquals(addressDto, result);
    }

    @Test
    void updateShouldCallValidatorAndDao() throws ValidationException, EntityNotFoundException, ForbiddenException {
        AddressDto addressDto = new AddressDto();
        addressDto.setId(1L);
        ApplicationUserDto userDto = new ApplicationUserDto();
        userDto.setAddress(addressDto);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("user@example.com");
        when(userDao.findByEmail("user@example.com")).thenReturn(userDto);
        when(addressDao.update(addressDto)).thenReturn(addressDto);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());

        AddressDto result = addressService.update(addressDto);

        verify(addressValidator).validateForUpdate(addressDto);
        verify(addressDao).update(addressDto);
        assertEquals(addressDto, result);
    }

    @Test
    void updateShouldThrowForbiddenException() {
        AddressDto addressDto = new AddressDto();
        addressDto.setId(1L);
        ApplicationUserDto userDto = new ApplicationUserDto();
        userDto.setAddress(new AddressDto());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("user@example.com");
        when(userDao.findByEmail("user@example.com")).thenReturn(userDto);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());

        assertThrows(ForbiddenException.class, () -> addressService.update(addressDto));
    }

    @Test
    void deleteShouldCallDao() throws ForbiddenException, EntityNotFoundException {
        AddressDto addressDto = new AddressDto();
        addressDto.setId(1L);
        ApplicationUserDto userDto = new ApplicationUserDto();
        userDto.setAddress(addressDto);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("user@example.com");
        when(userDao.findByEmail("user@example.com")).thenReturn(userDto);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());

        addressService.delete(1L);

        verify(addressDao).deleteById(1L);
    }

    @Test
    void deleteShouldThrowForbiddenException() {
        AddressDto addressDto = new AddressDto();
        addressDto.setId(1L);
        ApplicationUserDto userDto = new ApplicationUserDto();
        userDto.setAddress(new AddressDto());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("user@example.com");
        when(userDao.findByEmail("user@example.com")).thenReturn(userDto);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());

        assertThrows(ForbiddenException.class, () -> addressService.delete(1L));
    }

    @Test
    void deleteShouldThrowNotFoundException() throws EntityNotFoundException {
        AddressDto addressDto = new AddressDto();
        addressDto.setId(1L);
        ApplicationUserDto userDto = new ApplicationUserDto();
        userDto.setAddress(addressDto);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("user@example.com");
        when(userDao.findByEmail("user@example.com")).thenReturn(userDto);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        doThrow(new EntityNotFoundException(1L)).when(addressDao).deleteById(1L);

        assertThrows(NotFoundException.class, () -> addressService.delete(1L));
    }

    @Test
    void findByIdShouldReturnAddressDto() throws EntityNotFoundException {
        AddressDto addressDto = new AddressDto();
        when(addressDao.findById(1L)).thenReturn(addressDto);

        AddressDto result = addressService.findById(1L);

        verify(addressDao).findById(1L);
        assertEquals(addressDto, result);
    }

    @Test
    void findByIdShouldThrowNotFoundException() throws EntityNotFoundException {
        when(addressDao.findById(1L)).thenThrow(new EntityNotFoundException(1L));

        assertThrows(NotFoundException.class, () -> addressService.findById(1L));
    }

    @Test
    void findAllShouldReturnListOfAddresses() {
        List<AddressDto> addressDtos = Collections.singletonList(new AddressDto());
        when(addressDao.findAll()).thenReturn(addressDtos);

        List<AddressDto> result = addressService.findAll();

        verify(addressDao).findAll();
        assertEquals(addressDtos, result);
    }
}
