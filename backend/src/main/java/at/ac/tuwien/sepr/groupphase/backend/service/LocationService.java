package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.LocationSearch;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import java.util.List;
import org.springframework.data.domain.Page;

public interface LocationService {
    /**
     * Creates a new location.
     *
     * @param dto the data transfer object containing the location information
     * @return the created {@link LocationDto}
     */
    public LocationDto createLocation(LocationDto dto) throws ValidationException, ForbiddenException, DtoNotFoundException;

    /**
     * Updates an existing location.
     *
     * @param locationDto the data transfer object containing the updated location information
     * @return the updated {@link LocationDto}
     */
    LocationDto update(LocationDto locationDto) throws ValidationException, DtoNotFoundException, ForbiddenException;

    /**
     * Deletes an location by its ID.
     *
     * @param id the ID of the location to delete
     */
    void delete(Long id) throws DtoNotFoundException, ForbiddenException;

    /**
     * Finds an location by its ID.
     *
     * @param id the ID of the location to find
     * @return the found {@link LocationDto}, or {@code null} if no location is found with the given ID
     */
    LocationDto findById(Long id) throws DtoNotFoundException;

    /**
     * Retrieves all locationes.
     *
     * @return a list of {@link LocationDto} objects containing all locationes
     */
    Iterable<LocationDto> findAll();

    Page<LocationDto> search(LocationSearch locationSearchRequest);
}
