package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.InvoiceDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.InvoiceMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Invoice;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.InvoiceType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.InvoiceRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class InvoiceDao extends AbstractDao<Invoice, InvoiceDto> {
    public InvoiceDao(InvoiceRepository repository, InvoiceMapper mapper) {
        super(repository, mapper);
    }

    public List<InvoiceDto> findByOrderId(long id) {
        var found = ((InvoiceRepository) repository).findByOrderId(id);
        return mapper.toDto(found);
    }

    public InvoiceDto createCancellationInvoiceForOrder(long orderId) {
        return createInvoiceForOrder(orderId, InvoiceType.CANCELLATION);
    }

    public InvoiceDto createPurchaseInvoiceForOrder(long orderId) {
        return createInvoiceForOrder(orderId, InvoiceType.PURCHASE);
    }

    private InvoiceDto createInvoiceForOrder(long orderId, InvoiceType invoiceType) {
        var refOrder = new Order();
        refOrder.setId(orderId);
        var invoice = new Invoice();
        invoice.setOrder(refOrder);
        invoice.setInvoiceType(invoiceType);
        invoice.setDateTime(LocalDateTime.now());

        var res = repository.save(invoice);
        return mapper.toDto(res);
    }
}
