package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDetailsResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TicketResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping("/api/v1/ticket")
public class TicketEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TicketService ticketService;
    private final TicketResponseMapper ticketMapper;

    public TicketEndpoint(TicketService ticketService, TicketResponseMapper ticketMapper) {
        this.ticketService = ticketService;
        this.ticketMapper = ticketMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<TicketDetailsResponse> findById(@PathVariable("id") long id) {
        try {
            var ticket = ticketMapper.toResponse(ticketService.findById(id));
            return ResponseEntity.ok(ticket);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
    }
}
