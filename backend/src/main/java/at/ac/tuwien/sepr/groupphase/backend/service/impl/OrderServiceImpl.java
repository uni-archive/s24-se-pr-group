package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderSummaryDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.OrderDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.InvoiceService;
import at.ac.tuwien.sepr.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.OrderValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OrderDao orderDao;

    private final OrderValidator orderValidator;

    private final TicketService ticketService;

    private final InvoiceService invoiceService;

    public OrderServiceImpl(OrderDao orderDao, OrderValidator orderValidator, TicketService ticketService, InvoiceService invoiceService) {
        this.orderDao = orderDao;
        this.orderValidator = orderValidator;
        this.ticketService = ticketService;
        this.invoiceService = invoiceService;
    }

    @Override
    public OrderDetailsDto findById(long id, ApplicationUserDto user) throws EntityNotFoundException, ValidationException {
        LOGGER.trace("Get order details. Order-ID: {}, User: {}", id, user);
        var found = orderDao.findById(id);

        orderValidator.validateForFindById(found, user);

        addInvoicesToOrder(found);

        // add sectorShowInfo to each ticket
        found.getTickets().forEach(ticketService::loadSectorShowForTicket);
        return found;
    }

    @Override
    public List<OrderSummaryDto> findForUser(long userId) {
        LOGGER.trace("Get all orders for user. User-ID: {}", userId);
        var orders = orderDao.findForUser(userId);
        orders.forEach(this::addInvoicesToOrder);
        return orders;
    }

    @Override
    public void cancelOrder(long id, ApplicationUserDto user) throws EntityNotFoundException, ValidationException {
        LOGGER.trace("Cancel order for user. Order-ID: {}, User: {}", id, user);
        var order = orderDao.findById(id);

        orderValidator.validateForCancel(order, user);

        invoiceService.createCancellationInvoiceForOrder(order.getId());
        ticketService.invalidateAllTicketsForOrder(order.getId());
    }

    private void addInvoicesToOrder(OrderDetailsDto order) {
        order.setInvoices(invoiceService.findByOrderId(order.getId()));
    }

    private void addInvoicesToOrder(OrderSummaryDto order) {
        order.setInvoices(invoiceService.findByOrderId(order.getId()));
    }
}
