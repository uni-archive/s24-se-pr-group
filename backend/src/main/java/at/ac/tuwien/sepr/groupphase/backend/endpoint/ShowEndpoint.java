package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowListDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.util.Authority.Code;
import at.ac.tuwien.sepr.groupphase.backend.mapper.ShowMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.ShowService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.print.attribute.standard.Media;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/show")
public class ShowEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private ShowService service;
    private final ShowMapper showMapper;

    public ShowEndpoint(ShowService showService, ShowMapper showMapper) {
        this.service = showService;
        this.showMapper = showMapper;
    }

    @Secured(Code.ADMIN)
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createShow(@RequestBody ShowCreationDto creationDto) {
        LOGGER.info("POST /create {}", creationDto);
        return service.createShow(creationDto);
    }

    @PermitAll
    @GetMapping(value = "/event/{eventid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ShowListDto>> getShowsByEventId(@PathVariable("eventid") long eventid)
        throws EntityNotFoundException {
        var result = service.getShowsByEventId(eventid);
        LOGGER.info("POST: events/{} ({})", eventid, result);
        return ResponseEntity.ok(result);
    }

    @PermitAll
    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ShowListDto>> searchShows(@RequestBody ShowSearchDto searchDto)
        throws EntityNotFoundException {
        var result = service.searchShows(searchDto);
        LOGGER.info("POST: /search ({})", result);
        return ResponseEntity.ok(result);
    }

    @PermitAll
    @GetMapping("/location/{locationId}")
    public ResponseEntity<Page<ShowResponse>> getShowByLocation(@PathVariable("locationId") Long locationId,
        @RequestParam(value = "onlyFutureShows", required = false, defaultValue = "true") boolean onlyFutureShows,
        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
        @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(
            service.findByLocation(locationId, true, PageRequest.of(page, size)).map(showMapper::toResponse));
    }
}
