package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.InvoiceDto;
import at.ac.tuwien.sepr.groupphase.backend.mapper.InvoiceMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Invoice;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.InvoiceRepository;
import org.springframework.stereotype.Component;

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
}
