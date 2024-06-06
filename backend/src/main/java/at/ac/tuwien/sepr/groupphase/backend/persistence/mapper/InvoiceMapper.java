package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.InvoiceDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Invoice;
import org.mapstruct.Mapper;

@Mapper
public interface InvoiceMapper extends BaseEntityMapper<Invoice, InvoiceDto> {
}
