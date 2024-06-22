package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressSearch;
import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.LocationSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationSummaryResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.LocationResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.util.Authority.Code;
import at.ac.tuwien.sepr.groupphase.backend.service.LocationService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.HallPlanServiceImpl;
import jakarta.annotation.security.PermitAll;
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

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/location")
public class LocationEndpoint {

    private final LocationService locationService;
    private final LocationResponseMapper locationMapper;
    private final HallPlanServiceImpl hallPlanServiceImpl;

    public LocationEndpoint(LocationService locationService, LocationResponseMapper locationMapper, HallPlanServiceImpl hallPlanServiceImpl) {
        this.locationService = locationService;
        this.locationMapper = locationMapper;
        this.hallPlanServiceImpl = hallPlanServiceImpl;
    }

    @Secured(Code.ADMIN)
    @PostMapping
    public ResponseEntity<LocationDto> create(@RequestBody LocationCreateRequest createRequest)
        throws ValidationException, ForbiddenException {
        LocationDto createdLocation = null;
        try {
            createdLocation = locationService.createLocation(locationMapper.createRequestToDto(createRequest));
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
        return new ResponseEntity<>(createdLocation, HttpStatus.CREATED);
    }

    @Secured(Code.USER)
    @PutMapping("/{id}")
    public ResponseEntity<LocationDto> update(@PathVariable(name = "id") Long id, @RequestBody LocationDto locationDto)
        throws ValidationException, ForbiddenException {
        locationDto.setId(id);
        LocationDto updatedLocation = null;
        try {
            updatedLocation = locationService.update(locationDto);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
        return new ResponseEntity<>(updatedLocation, HttpStatus.OK);
    }

    @Secured(Code.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id)
        throws ForbiddenException {
        try {
            locationService.delete(id);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermitAll
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LocationDto> findById(@PathVariable(name = "id") Long id) {
        LocationDto location = null;
        try {
            location = locationService.findById(id);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
        return new ResponseEntity<>(location, HttpStatus.OK);
    }

    @Secured(Code.ADMIN)
    @GetMapping
    public ResponseEntity<Iterable<LocationDto>> findAll() {
        Iterable<LocationDto> locations = locationService.findAll();
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @PermitAll
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Page<LocationSummaryResponse>> search(
        @RequestParam(name = "name", required = false) String name,
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
        Page<LocationSummaryResponse> search = locationService.search(locationSearchRequest).map(locationMapper::toSummaryResponse);
        return new ResponseEntity<>(search, HttpStatus.OK);
    }

    @Secured(Code.USER)
    @GetMapping(value = "/name/{name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<LocationDto>> findByName(@PathVariable(name = "name") String name) {
        LocationSearch locationSearchRequest = new LocationSearch(name, new AddressSearch("", "", "", ""), false,
            PageRequest.of(0, 10));
        return new ResponseEntity<>(locationService.search(locationSearchRequest).stream().toList(), HttpStatus.OK);
    }

    @Secured(Code.USER)
    @GetMapping(value = "/name/")
    public ResponseEntity<List<LocationDto>> findByName() {
        return findByName("");
    }
}
