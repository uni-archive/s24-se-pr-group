package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ArtistSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistSearchResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ArtistResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping("/api/v1/artists")
public class ArtistEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistService artistService;
    private final ArtistResponseMapper artistResponseMapper;

    public ArtistEndpoint(ArtistService artistService, ArtistResponseMapper artistResponseMapper) {
        this.artistService = artistService;
        this.artistResponseMapper = artistResponseMapper;
    }

    @PermitAll
    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ArtistSearchResponse>> search(
        @RequestParam(name = "firstname", required = false) String firstName,
        @RequestParam(name = "lastname", required = false) String lastName,
        @RequestParam(name = "artistName", required = false) String artistName,
        @RequestParam(name = "page", defaultValue = "0") Integer page,
        @RequestParam(name = "size", defaultValue = "15") Integer size,
        @RequestParam(name = "sort", defaultValue = "artistName,firstName,lastName") String sort
    ) {
        Sort sortBy = Sort.by(sort.split(",")[0]);
        if (sort.split(",").length > 1) {
            if (sort.split(",")[1].equals("asc")) {
                sortBy = sortBy.ascending();
            } else if (sort.split(",")[1].equals("desc")) {
                sortBy = sortBy.descending();
            }
        }

        PageRequest pageable = PageRequest.of(page, size, sortBy);
        var searchDto = new ArtistSearchDto(firstName, lastName, artistName, pageable);
        var res = artistService.search(searchDto)
            .map(artistResponseMapper::toResponse);
        return ResponseEntity.ok(res);


    }


    @PermitAll
    @GetMapping(value = "/artist/{artistId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArtistDto> findById(@PathVariable("artistId") long artistId) {
        try {
            return ResponseEntity.ok(artistService.findById(artistId));
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
    }
}
