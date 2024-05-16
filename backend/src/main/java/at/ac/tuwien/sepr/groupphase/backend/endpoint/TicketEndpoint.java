package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDetailsResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TicketResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TicketService ticketService;
    private final TicketResponseMapper ticketMapper;

    private final UserService userService;

    public TicketEndpoint(TicketService ticketService, TicketResponseMapper ticketMapper, UserService userService) {
        this.ticketService = ticketService;
        this.ticketMapper = ticketMapper;
        this.userService = userService;
    }

    @Secured("ROLE_USER")
    @GetMapping(path = "/ticket/{id}", produces = "application/json")
    public ResponseEntity<TicketDetailsResponse> findById(@PathVariable("id") long id) {
        try {
            var ticket = ticketMapper.toResponse(ticketService.findById(id));
            return ResponseEntity.ok(ticket);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    @Secured("ROLE_USER")
    @GetMapping(path = "/mytickets", produces = "application/json")
    public ResponseEntity<List<TicketDetailsResponse>> findForUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getPrincipal().toString();
        var user = userService.findApplicationUserByEmail(username);
        var tickets = ticketService.findForUserById(user.getId());
        return ResponseEntity.ok(ticketMapper.toResponseList(tickets));
    }

}
