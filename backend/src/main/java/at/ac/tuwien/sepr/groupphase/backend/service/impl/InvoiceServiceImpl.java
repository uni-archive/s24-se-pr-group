package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.InvoiceDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.InvoiceDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.InvoiceService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final InvoiceDao invoiceDao;

    public InvoiceServiceImpl(InvoiceDao invoiceDao) {
        this.invoiceDao = invoiceDao;
    }

    @Override
    public List<InvoiceDto> findByOrderId(long id) {
        return invoiceDao.findByOrderId(id);
    }

    @Override
    public InvoiceDto createCancellationInvoiceForOrder(long orderId) {
        LOGGER.trace("Create cancellation invoice for order. Order-ID: {}", orderId);
        return invoiceDao.createCancellationInvoiceForOrder(orderId);
    }

    @Override
    public InvoiceDto createPurchaseInvoiceForOrder(long orderId) {
        LOGGER.trace("Create purchase invoice for order. Order-ID: {}", orderId);
        return invoiceDao.createPurchaseInvoiceForOrder(orderId);
    }


}
