package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.InvoiceDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;

import java.util.List;

public interface InvoiceService {
    /**
     * Finds all (up to 2) invoices related to an order.
     *
     * @param id the order-id
     * @return List of {@link InvoiceDto}
     */
    List<InvoiceDto> findByOrderId(long id);


    /**
     * Creates a new cancellation invoice for an order.
     *
     * @param orderId the id of the order.
     * @return the created invoice.
     */
    InvoiceDto createCancellationInvoiceForOrder(long orderId);


    /**
     * Creates a new purchase invoice for an order.
     *
     * @param orderId the id of the order.
     * @return the created invoice.
     */
    InvoiceDto createPurchaseInvoiceForOrder(long orderId);
}
