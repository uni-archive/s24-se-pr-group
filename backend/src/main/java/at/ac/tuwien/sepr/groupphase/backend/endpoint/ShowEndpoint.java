package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowListDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.ShowService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.print.attribute.standard.Media;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/shows")
public class ShowEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private ShowService service;

    public ShowEndpoint(ShowService service) {
        this.service = service;
    }

    @PermitAll
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createShow(@RequestBody ShowCreationDto creationDto) {
        LOGGER.info("POST /create {}", creationDto);
        return service.createShow(creationDto);
    }

    @PermitAll
    @GetMapping(value = "/{eventid}", produces = MediaType.APPLICATION_JSON_VALUE)
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

}
