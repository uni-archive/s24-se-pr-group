package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EventSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EventWithTicketCountDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.EventValidator;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/events")
public class EventEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private EventService service;

    private EventResponseMapper mapper;

    public EventEndpoint(EventService service, EventResponseMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventDto> createEvent(@RequestBody EventCreationDto createDto) {
        return service.createEvent(createDto);
    }

    @PermitAll
    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EventDto>> searchEvents(@RequestBody EventSearchDto searchDto) {
        return ResponseEntity.ok(service.searchEvents(searchDto));
    }

    @PermitAll
    @GetMapping(value = "/{eventid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventDto> findById(@PathVariable("eventid") long eventid) throws EntityNotFoundException {
        return ResponseEntity.ok(service.getById(eventid));
    }

    @PermitAll
    @GetMapping(value = "/by-artist/{artistId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EventResponse>> findByArtist(@PathVariable("artistId") long artistId) {
        var found = service.findByArtist(artistId);
        var mapped = mapper.toResponseList(found);
        return ResponseEntity.ok(mapped);
    }

    @PermitAll
    @GetMapping("/top10")
    public ResponseEntity<List<EventWithTicketCountDto>> getTop10EventsWithMostTickets() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<EventWithTicketCountDto> events = service.getTop10EventsWithMostTickets(pageRequest);
        return ResponseEntity.ok(events);
    }
}
