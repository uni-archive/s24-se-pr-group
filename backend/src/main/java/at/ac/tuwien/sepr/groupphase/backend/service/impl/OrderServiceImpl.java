package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.OrderDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.InvoiceService;
import at.ac.tuwien.sepr.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OrderDao orderDao;

    private final TicketService ticketService;

    private final InvoiceService invoiceService;

    public OrderServiceImpl(OrderDao orderDao, TicketService ticketService, InvoiceService invoiceService) {
        this.orderDao = orderDao;
        this.ticketService = ticketService;
        this.invoiceService = invoiceService;
    }

    @Override
    public OrderDetailsDto findById(long id) throws EntityNotFoundException {
        LOGGER.debug("Get order details. Order-ID: {}", id);
        var found = orderDao.findById(id);

        // add invoices to order
        found.setInvoices(invoiceService.findByOrderId(found.getId()));

        // add sectorShowInfo to each ticket
        found.getTickets().forEach(ticketService::loadSectorShowForTicket);
        return found;
    }
}
