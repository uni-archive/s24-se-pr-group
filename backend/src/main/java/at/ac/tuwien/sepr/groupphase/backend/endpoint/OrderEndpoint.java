package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDetailsResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.HallSectorShowResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.OrderDetailsResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping("/api/v1/order")
public class OrderEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OrderService orderService;
    private final OrderDetailsResponseMapper orderMapper;

    public OrderEndpoint(OrderService orderService, OrderDetailsResponseMapper orderMapper, HallSectorShowResponseMapper fuck) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<OrderDetailsResponse> findById(@PathVariable("id") long id) {
        OrderDetailsResponse order;
        try {
            order = orderMapper.toResponse(orderService.findById(id));
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e);
        }
        return ResponseEntity.ok(order);
    }
}
