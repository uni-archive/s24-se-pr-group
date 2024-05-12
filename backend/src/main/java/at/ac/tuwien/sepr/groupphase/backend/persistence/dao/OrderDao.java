package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.mapper.OrderMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.OrderRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderDao extends AbstractDao<Order, OrderDetailsDto> {
    public OrderDao(OrderRepository repository, OrderMapper mapper) {
        super(repository, mapper);
    }

    public OrderDetailsDto findById(long id) throws EntityNotFoundException {
        var opt = ((OrderRepository) repository).findById(id);
        var found = opt.orElseThrow(() -> new EntityNotFoundException(id));
        return mapper.toDto(found);
    }
}
