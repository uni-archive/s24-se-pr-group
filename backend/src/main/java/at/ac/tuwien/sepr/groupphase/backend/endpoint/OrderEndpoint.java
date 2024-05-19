package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDetailsResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderSummaryResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.HallSectorShowResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.OrderResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OrderService orderService;
    private final OrderResponseMapper orderMapper;

    private final UserService userService;

    public OrderEndpoint(OrderService orderService, OrderResponseMapper orderMapper, HallSectorShowResponseMapper fuck, UserService userService) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.userService = userService;
    }

    @Secured("ROLE_USER")
    @GetMapping(path = "/order/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDetailsResponse> findById(@PathVariable("id") long id) {
        OrderDetailsResponse order;
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            var username = authentication.getPrincipal().toString();
            var user = userService.findApplicationUserByEmail(username);

            order = orderMapper.toResponse(orderService.findById(id, user));
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e);
        } catch (ValidationException e) {
            throw new at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.ValidationException(e);
        }
        return ResponseEntity.ok(order);
    }

    @Secured("ROLE_USER")
    @GetMapping(path = "/myorders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderSummaryResponse>> findForUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getPrincipal().toString();
        var user = userService.findApplicationUserByEmail(username);
        var res = orderService.findForUser(user.getId());
        return ResponseEntity.ok(orderMapper.toSummaryResponse(res));
    }

    @Secured("ROLE_USER")
    @DeleteMapping(path = "/order/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable("id") long orderId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getPrincipal().toString();
        var user = userService.findApplicationUserByEmail(username);
        try {
            orderService.cancelOrder(orderId, user);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e);
        } catch (ValidationException e) {
            throw new at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.ValidationException(e);
        }
    }
}
