package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.util.Authority.Code;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.AddressDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.UserDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.AbstractService;
import at.ac.tuwien.sepr.groupphase.backend.service.AddressService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.AddressValidator;
import java.util.List;
import java.util.Objects;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl extends AbstractService<AddressDto> implements AddressService {

    private final AddressDao addressDao;
    private final AddressValidator addressValidator;
    private final UserDao userDao;

    public AddressServiceImpl(AddressDao addressDao, AddressValidator addressValidator, UserDao userDao) {
        super(addressValidator, addressDao);
        this.addressDao = addressDao;
        this.addressValidator = addressValidator;
        this.userDao = userDao;
    }

    @Override
    public AddressDto update(AddressDto addressDto)
        throws ValidationException, EntityNotFoundException, ForbiddenException {
        addressValidator.validateForUpdate(addressDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUserEmail = authentication.getName();
        ApplicationUserDto byEmail = userDao.findByEmail(loggedInUserEmail);
        if (!isAdminOrUpdatingOwnAddress(addressDto.getId(), authentication, byEmail)) {
            throw new ForbiddenException();
        }
        return addressDao.update(addressDto);
    }


    private static boolean isAdminOrUpdatingOwnAddress(Long addressId, Authentication authentication,
        ApplicationUserDto currentUser) {
        return authentication.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals(Code.ADMIN))
            || (Objects.nonNull(currentUser) && Objects.nonNull(currentUser.getAddress()) && Objects.equals(
                currentUser.getAddress().getId(), addressId));
    }

    @Override
    public void delete(Long id) throws ForbiddenException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String loggedInUserEmail = authentication.getName();
            ApplicationUserDto byEmail = userDao.findByEmail(loggedInUserEmail);
            if (!isAdminOrUpdatingOwnAddress(id, authentication, byEmail)) {
                throw new ForbiddenException();
            }
            addressDao.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    @Override
    public AddressDto findById(Long id) {
        try {
            return addressDao.findById(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    @Override
    public List<AddressDto> findAll() {
        return addressDao.findAll();
    }
}
