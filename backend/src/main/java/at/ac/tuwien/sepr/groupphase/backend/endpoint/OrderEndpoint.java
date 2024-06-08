package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.config.SecurityPropertiesConfig.Auth;
import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDetailsResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderSummaryResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.OrderResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.util.Authority.Code;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandles;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OrderService orderService;
    private final OrderResponseMapper orderMapper;

    private final UserService userService;
    private final Auth auth;

    public OrderEndpoint(OrderService orderService, OrderResponseMapper orderMapper, UserService userService,
        Auth auth) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.userService = userService;
        this.auth = auth;
    }

    @Secured(Code.USER)
    @GetMapping(path = "/order/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDetailsResponse> findById(@PathVariable("id") long id) {
        OrderDetailsResponse order;
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            var username = authentication.getPrincipal().toString();
            var user = userService.findApplicationUserByEmail(username);

            order = orderMapper.toResponse(orderService.findById(id, user));
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        } catch (ValidationException e) {
            throw new at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.ValidationException(e);
        }
        return ResponseEntity.ok(order);
    }

    @Secured(Code.USER)
    @GetMapping(path = "/myorders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderSummaryResponse>> findForUser() throws DtoNotFoundException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getPrincipal().toString();
        var user = userService.findApplicationUserByEmail(username);
        var res = orderService.findForUser(user.getId());
        return ResponseEntity.ok(orderMapper.toSummaryResponse(res));
    }

    @Secured(Code.USER)
    @DeleteMapping(path = "/order/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable("id") long orderId) {
        var user = getUserFromSecurityContext();
        try {
            orderService.cancelOrder(orderId, user);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e);
        } catch (ValidationException e) {
            throw new at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.ValidationException(e);
        }
    }

    @Secured(Code.USER)
    @PutMapping("/order/{id}")
    public void purchaseOrder(@PathVariable("id") long orderId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getPrincipal().toString();
        try {
            var user = userService.findApplicationUserByEmail(username);
            orderService.purchaseOrder(orderId, user);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        } catch (ValidationException e) {
            throw new at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.ValidationException(e);
        }
    }

    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OrderDetailsResponse> createOrder(HttpServletResponse response) throws ValidationException {
        ApplicationUserDto user = null;
        try {
            user = getUserFromSecurityContext();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        OrderDetailsDto orderDetailsDto = orderService.create(user);
        response.addCookie(new Cookie("order", orderDetailsDto.getId().toString()));
        return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toResponse(orderDetailsDto));
    }

    private ApplicationUserDto getUserFromSecurityContext() throws NotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        try {
            return userService.findApplicationUserByEmail(username);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
    }
}
