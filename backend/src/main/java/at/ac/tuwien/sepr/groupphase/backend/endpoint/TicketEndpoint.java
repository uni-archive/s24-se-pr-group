package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorTicketCreationRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketCreationRequest;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDetailsResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TicketResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.util.Authority.Code;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @Secured(Code.USER)
    @GetMapping(path = "/ticket/{id}", produces = "application/json")
    public ResponseEntity<TicketDetailsResponse> findById(@PathVariable("id") long id) {
        try {
            var ticket = ticketMapper.toResponse(ticketService.findById(id));
            return ResponseEntity.ok(ticket);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    @Secured(Code.USER)
    @GetMapping(path = "/mytickets", produces = "application/json")
    public ResponseEntity<List<TicketDetailsResponse>> findForUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getPrincipal().toString();
        ApplicationUserDto user = null;
        try {
            user = userService.findApplicationUserByEmail(username);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
        var tickets = ticketService.findForUserById(user.getId());
        return ResponseEntity.ok(ticketMapper.toResponseList(tickets));
    }

    @Secured(Code.USER)
    @DeleteMapping(path = "/ticket/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReservedTicket(
        @PathVariable("id") long id
    ) throws NotFoundException, ValidationException {
        try {
            ticketService.cancelReservedTicket(id);
        } catch (at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException e) {
            throw new ValidationException(e);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    @Secured(Code.USER)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketDetailsResponse addTicket(@RequestBody TicketCreationRequest ticketCreationRequest)
        throws at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException, ForbiddenException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getPrincipal().toString();
        ApplicationUserDto user = null;
        try {
            user = userService.findApplicationUserByEmail(username);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
        try {
            return ticketMapper.toResponse(ticketService.addTicketToOrder(ticketMapper.toDto(ticketCreationRequest), user));
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    @Secured(Code.USER)
    @PostMapping("/for-section")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketDetailsResponse addSectionTicket(@RequestBody SectorTicketCreationRequest ticketCreationRequest)
        throws at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException, ForbiddenException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getPrincipal().toString();
        ApplicationUserDto user = null;
        try {
            user = userService.findApplicationUserByEmail(username);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
        try {
            return ticketMapper.toResponse(ticketService.addSectorTicketToOrder(ticketMapper.toSectorDto(ticketCreationRequest), user));
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    @Secured(Code.USER)
    @DeleteMapping("/ticket/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeTicket(@PathVariable("id") long ticketId)
        throws at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException, ForbiddenException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getPrincipal().toString();
        ApplicationUserDto user = null;
        try {
            user = userService.findApplicationUserByEmail(username);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }

        ticketService.deleteTicket(ticketId, user);
    }

    @Secured(Code.USER)
    @PutMapping("/ticket/{id}")
    public void changeTicketReserved(@PathVariable("id") long ticketId, @RequestBody boolean setReserved)
        throws at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException, ForbiddenException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getPrincipal().toString();
        ApplicationUserDto user = null;
        try {
            user = userService.findApplicationUserByEmail(username);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }

        try {
            ticketService.changeTicketReserved(ticketId, setReserved, user);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    @GetMapping(path = "/show/{id}")
    @Secured(Code.ADMIN)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<TicketDetailsResponse>> searchTicketsInShow(@PathVariable("id") long id,
        @RequestParam(value = "firstName", defaultValue = "") String firstName,
        @RequestParam(value = "familyName", defaultValue = "") String familyName,
        @RequestParam(value = "reservedOnly", defaultValue = "true") boolean reservedOnly,
        @RequestParam(value = "valid", defaultValue = "false") boolean valid,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(
            ticketService.search(new TicketSearchDto(id, firstName, familyName, reservedOnly, valid, PageRequest.of(page, size)))
                .map(ticketMapper::toResponse));
    }

    @PostMapping(path = "/{id}/validate")
    @Secured(Code.ADMIN)
    @ResponseStatus(HttpStatus.OK)
    public void validateTicket(@PathVariable("id") long id) throws NotFoundException, ValidationException {
        try {
            ticketService.validateTicket(id);
        } catch (at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException e) {
            throw new ValidationException(e);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
    }
}
