package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;

/**
 *  Handles creating, cancelling and fetching of orders made
 *  by a user.
 */
public interface OrderService  {

    /**
     * Gets an order with detailed information about each ticket that is included.
     *
     * @param id The order-id
     * @return {@link OrderDetailsDto}
     * @throws EntityNotFoundException if no order with this id exists.
     */
    OrderDetailsDto findById(long id) throws EntityNotFoundException;
}
