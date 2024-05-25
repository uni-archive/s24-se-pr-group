package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressSearch;
import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.LocationSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.LocationResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.util.Authority.Code;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.LocationService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/location")
public class LocationEndpoint {

    private final LocationService locationService;
    private final LocationResponseMapper locationMapper;

    public LocationEndpoint(LocationService locationService, LocationResponseMapper locationMapper) {
        this.locationService = locationService;
        this.locationMapper = locationMapper;
    }

    @Secured(Code.ADMIN)
    @PostMapping
    public ResponseEntity<LocationDto> create(@RequestBody LocationCreateRequest createRequest)
        throws ValidationException, ForbiddenException {
        LocationDto createdLocation = locationService.create(locationMapper.createRequestToDto(createRequest));
        return new ResponseEntity<>(createdLocation, HttpStatus.CREATED);
    }

    @Secured(Code.USER)
    @PutMapping("/{id}")
    public ResponseEntity<LocationDto> update(@PathVariable(name = "id") Long id, @RequestBody LocationDto locationDto)
        throws ValidationException, EntityNotFoundException, ForbiddenException {
        locationDto.setId(id);
        LocationDto updatedLocation = locationService.update(locationDto);
        return new ResponseEntity<>(updatedLocation, HttpStatus.OK);
    }

    @Secured(Code.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id)
        throws EntityNotFoundException, ForbiddenException {
        locationService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured(Code.USER)
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

    public ResponseEntity<LocationDto> findById(@PathVariable(name = "id") Long id) throws EntityNotFoundException {
        LocationDto location = locationService.findById(id);
        return new ResponseEntity<>(location, HttpStatus.OK);
    }

    @Secured(Code.ADMIN)
    @GetMapping
    public ResponseEntity<Iterable<LocationDto>> findAll() {
        Iterable<LocationDto> locations = locationService.findAll();
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @Secured(Code.USER)
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Page<LocationDto>> search(@RequestParam(name = "name", required = false) String name,
        @RequestParam(name = "city", required = false) String city,
        @RequestParam(name = "street", required = false) String street,
        @RequestParam(name = "postalCode", required = false) String postalCode,
        @RequestParam(name = "country", required = false) String country,
        @RequestParam(name = "page", defaultValue = "0") Integer page,
        @RequestParam(name = "size", defaultValue = "15") Integer size,
        @RequestParam(name = "sort", defaultValue = "name") String sort,
        @RequestParam(name = "withUpComingShows", defaultValue = "false") Boolean withUpComingShows) {
        AddressSearch addressSearch = new AddressSearch(city, street, postalCode, country);

        Sort sortBy = Sort.by(sort.split(",")[0]);
        if (sort.split(",").length > 1) {
            if (sort.split(",")[1].equals("asc")) {
                sortBy = sortBy.ascending();
            } else if (sort.split(",")[1].equals("desc")) {
                sortBy = sortBy.descending();
            }
        }
        PageRequest pageable = PageRequest.of(page, size, sortBy);
        LocationSearch locationSearchRequest = new LocationSearch(name, addressSearch, withUpComingShows, pageable);
        return new ResponseEntity<>(locationService.search(locationSearchRequest), HttpStatus.OK);
    }
}
