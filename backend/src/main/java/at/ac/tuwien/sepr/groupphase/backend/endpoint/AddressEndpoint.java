package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AddressCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.AddressResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.util.Authority.Code;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.AddressService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/address")
public class AddressEndpoint {

    private final AddressService addressService;
    private final AddressResponseMapper addressMapper;

    public AddressEndpoint(AddressService addressService, AddressResponseMapper addressResponseMapper) {
        this.addressService = addressService;
        this.addressMapper = addressResponseMapper;
    }

    @Secured(Code.ADMIN)
    @PostMapping
    public ResponseEntity<AddressDto> create(@RequestBody AddressCreateRequest createRequest)
        throws ValidationException, ForbiddenException {
        AddressDto createdAddress = addressService.create(addressMapper.createRequestToDto(createRequest));
        return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
    }

    @Secured(Code.USER)
    @PutMapping("/{id}")
    public ResponseEntity<AddressDto> update(@PathVariable(name = "id") Long id, @RequestBody AddressDto addressDto)
        throws ValidationException, EntityNotFoundException, ForbiddenException {
        addressDto.setId(id);
        AddressDto updatedAddress = null;
        try {
            updatedAddress = addressService.update(addressDto);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
        return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }

    @Secured(Code.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id)
        throws EntityNotFoundException, ForbiddenException {
        try {
            addressService.delete(id);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured(Code.USER)
    @GetMapping("/{id}")
    public ResponseEntity<AddressDto> findById(@PathVariable(name = "id") Long id) throws EntityNotFoundException {
        AddressDto address = null;
        try {
            address = addressService.findById(id);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @Secured(Code.ADMIN)
    @GetMapping
    public ResponseEntity<List<AddressDto>> findAll() {
        List<AddressDto> addresses = addressService.findAll();
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }
}
