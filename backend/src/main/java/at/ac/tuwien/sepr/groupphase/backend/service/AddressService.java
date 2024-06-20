package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import java.util.List;

/**
 * Service interface for managing addresses.
 */
public interface AddressService {

    /**
     * Creates a new address.
     *
     * @param addressDto the data transfer object containing the address information
     * @return the created {@link AddressDto}
     */
    AddressDto create(AddressDto addressDto) throws ValidationException, ForbiddenException;

    /**
     * Updates an existing address.
     *
     * @param addressDto the data transfer object containing the updated address information
     * @return the updated {@link AddressDto}
     */
    AddressDto update(AddressDto addressDto) throws ValidationException, DtoNotFoundException, ForbiddenException;

    /**
     * Deletes an address by its ID.
     *
     * @param id the ID of the address to delete
     */
    void delete(Long id) throws DtoNotFoundException, ForbiddenException;

    /**
     * Finds an address by its ID.
     *
     * @param id the ID of the address to find
     * @return the found {@link AddressDto}, or {@code null} if no address is found with the given ID
     */
    AddressDto findById(Long id) throws DtoNotFoundException;

    /**
     * Retrieves all addresses.
     *
     * @return a list of {@link AddressDto} objects containing all addresses
     */
    List<AddressDto> findAll();
}
