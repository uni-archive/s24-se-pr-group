package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderSummaryDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Handles creating, cancelling and fetching of orders made by a user.
 */
public interface OrderService {

    /**
     * Gets an order with detailed information about each ticket that is included.
     *
     * @param id The order-id
     * @return {@link OrderDetailsDto}
     * @throws DtoNotFoundException if no order with this id exists.
     * @throws ValidationException  if the customer of the found order does not match with the provided user.
     */
    OrderDetailsDto findById(long id, ApplicationUserDto user) throws DtoNotFoundException, ValidationException;

    /**
     * Same as findById but doesn't check for user-access AND DOES NOT LOAD HALLSECTORSHOW
     * SHOULD ONLY BE USED FOR THE JOB-REFRESH WHEN ADDING A TICKET
     * USE {@link #findById(long, ApplicationUserDto)} INSTEAD FOR OTHER USE-CASES
     *
     * @param id The order-id
     * @return {@link OrderDetailsDto}
     * @throws DtoNotFoundException if no order with this id exists.
     */
    OrderDetailsDto findByIdForJobRefresh(long id) throws DtoNotFoundException;


    /**
     * Finds all orders created by a user.
     *
     * @param userId the id of the user.
     * @return a list of {@link OrderDetailsDto} associated with the user.
     */
    List<OrderSummaryDto> findForUser(long userId);


    Page<OrderSummaryDto> findForUserPaged(long userId, Pageable pageable);

    /**
     * Cancels an order. This creates a cancellation-invoice and invalidates all tickets that are associated with this
     * order.
     *
     * @param id   the id of the order.
     * @param user the user who wants to cancel their order.
     * @throws EntityNotFoundException if no order with the given ID was found.
     * @throws ValidationException     if the user is not permitted to cancel the given order.
     */
    void cancelOrder(long id, ApplicationUserDto user) throws EntityNotFoundException, ValidationException;

    /**
     * Creates an order for the given user.
     *
     * @param user the user for which the order should be created - usually the logged in user
     * @return the created order
     */
    OrderDetailsDto create(ApplicationUserDto user) throws ValidationException;

    /**
     * Purchases an order. This creates a purchase-invoice and sets all tickets as valid that are associated with this order.
     *
     * @param id   the id of the order.
     * @param user the user who wants to purchase their order.
     * @throws DtoNotFoundException if no order with the given ID was found.
     * @throws ValidationException  if the user is not permitted to purchase the given order.
     */
    void purchaseOrder(long id, ApplicationUserDto user) throws DtoNotFoundException, ValidationException;
}
