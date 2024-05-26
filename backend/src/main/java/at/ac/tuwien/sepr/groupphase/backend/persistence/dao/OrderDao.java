package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderSummaryDto;
import at.ac.tuwien.sepr.groupphase.backend.mapper.CycleAvoidingMappingContext;
import at.ac.tuwien.sepr.groupphase.backend.mapper.OrderMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderDao extends AbstractDao<Order, OrderDetailsDto> {
    public OrderDao(OrderRepository repository, OrderMapper mapper) {
        super(repository, mapper);
    }


    @Transactional
    public OrderDetailsDto findById(long id) throws EntityNotFoundException {
        var opt = ((OrderRepository) repository).findById(id);
        var found = opt.orElseThrow(() -> new EntityNotFoundException(id));
        return mapper.toDto(found);
    }

    public List<OrderSummaryDto> findForUser(long userId) {
        var found = ((OrderRepository) repository).findForUser(userId);
        var res = new ArrayList<OrderSummaryDto>();
        for (var f : found) {
            var order = (Order) f[0];
            var ticketCount = (int) f[1];
            var totalPrice = (long) f[2];
            var mapped = ((OrderMapper) mapper).toSummaryDto(order);
            mapped.setTicketCount(ticketCount);
            mapped.setTotalPrice(totalPrice);
            res.add(mapped);
        }
        return res;
    }
}
