package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.InvoiceDto;

import java.util.List;

public interface InvoiceService {
    /**
     * Finds all (up to 2) invoices related to an order.
     *
     * @param id the order-id
     * @return List of {@link InvoiceDto}
     */
    List<InvoiceDto> findByOrderId(long id);
}
