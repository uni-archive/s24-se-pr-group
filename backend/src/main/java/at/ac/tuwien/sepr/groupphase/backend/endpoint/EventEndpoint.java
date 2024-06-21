package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EventSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EventType;
import at.ac.tuwien.sepr.groupphase.backend.dto.EventWithTicketCountDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok(service.createEvent(mapper.toDto(createDto)));
    }

    @PermitAll
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<EventResponse>> searchEvents(
        @RequestParam(name = "textSearch", required = false) String textSearch,
        @RequestParam(name = "typ", required = false) EventType typ,
        @RequestParam(name = "dauer", defaultValue = "0") Long dauer,
        @RequestParam(name = "page", defaultValue = "0") Integer page,
        @RequestParam(name = "size", defaultValue = "15") Integer size,
        @RequestParam(name = "sort", defaultValue = "title,description") String sort
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

        var eventSearch = new EventSearchDto(dauer, typ, textSearch, pageable);

        Page<EventResponse> search = service.searchEvents(eventSearch)
            .map(mapper::toResponse);
        return ResponseEntity.ok(search);
    }

    @PermitAll
    @GetMapping(value = "/{eventid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventDto> findById(@PathVariable("eventid") long eventid) {
        try {
            return ResponseEntity.ok(service.getById(eventid));
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
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
        List<EventWithTicketCountDto> events = service.getTop10EventsWithMostTickets();
        return ResponseEntity.ok(events);
    }
}
