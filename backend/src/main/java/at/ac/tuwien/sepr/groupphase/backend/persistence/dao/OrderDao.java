package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderSummaryDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.CycleAvoidingMappingContext;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.OrderMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.TicketMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDao extends AbstractDao<Order, OrderDetailsDto> {

    private final TicketMapper ticketMapper;

    public OrderDao(OrderRepository repository, OrderMapper mapper, TicketMapper ticketMapper) {
        super(repository, mapper);
        this.ticketMapper = ticketMapper;
    }


    @Transactional
    public OrderDetailsDto findById(long id) throws EntityNotFoundException {
        var opt = ((OrderRepository) repository).findById(id);
        var found = opt.orElseThrow(() -> new EntityNotFoundException(id));
        var order = ((OrderMapper) mapper).toDto(found);

        order.setTickets(found.getTickets()
            .stream()
            .map(ticketMapper::toDto)
            .toList());

        // return ((OrderMapper ) mapper).toDtoBla(found, new CycleAvoidingMappingContext());
        return order;
    }

    @Transactional
    public OrderSummaryDto findSummaryById(long id) throws EntityNotFoundException {
        var opt = ((OrderRepository) repository).findSummaryById(id);
        var found = opt.orElseThrow(() -> new EntityNotFoundException(id));
        return mapToOrderSummary(found);
    }

    public List<OrderSummaryDto> findForUser(long userId) {
        var found = ((OrderRepository) repository).findForUser(userId);
        return found.stream().map(this::mapToOrderSummary).collect(Collectors.toList());
    }

    public Page<OrderSummaryDto> findForUserPaged(long userId, Pageable pageable) {
        var found = ((OrderRepository) repository).findForUserPaged(userId, pageable);
        return found.map(o -> {
            var summary = ((OrderMapper) mapper).toSummaryDto(o);
            var ticketCount = ((OrderRepository) repository).getTicketCount(o.getId());
            summary.setTicketCount(ticketCount);

            var totalPriceNonReserved = ((OrderRepository) repository).getTotalNonReservedTicketPrice(o.getId());
            summary.setTotalPriceNonReserved(totalPriceNonReserved.stream().mapToLong(i -> i).sum());

            var totalPriceReserved = ((OrderRepository) repository).getTotalReservedTicketPrice(o.getId());
            summary.setTotalPriceReserved(totalPriceReserved.orElse(0));

            var totalOpenReservedPrice = ((OrderRepository) repository).getTotalOpenReservedTicketPrice(o.getId());
            summary.setTotalPriceOpenReserved(totalOpenReservedPrice.orElse(0));

            return summary;
        });
    }

    /**
     * The order summary contains aggregated data (currently the ticketCount and totalPrice)
     * Because of that its content diverges from the fields of the Entity and cannot be mapped
     * directly from JPA. Therefore, these endpoints return an Object[], currently consisting of 3 elements:
     * <ol>
     * <li> (idx 0) The Order entity object
     * <li> (idx 1) The total ticket-count for that order
     * <li> (idx 2) The total price (without taxes) for that order
     * </ol>
     * This method simply maps that data to the {@link OrderSummaryDto}, so that you don't
     * have to know anything about what I just wrote.
     * <br/>
     * Note that this method (currently) doesn't do any bounds or type-checking,
     * so it will throw if the provided input-data is wrong.
     *
     * @param data The array described as above.
     * @return {@link OrderSummaryDto}
     */
    private OrderSummaryDto mapToOrderSummary(Object[] data) {
        var order = (Order) data[0];
        var ticketCount = (int) data[1];
        var totalPriceNonReserved = (long) data[2];
        var totalPriceReserved = (long) data[3];
        var mapped = ((OrderMapper) mapper).toSummaryDto(order);
        mapped.setTicketCount(ticketCount);
        mapped.setTotalPriceNonReserved(totalPriceNonReserved);
        mapped.setTotalPriceReserved(totalPriceReserved);
        return mapped;
    }
}
