package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.InvoiceType;

import java.time.LocalDateTime;

public record InvoiceResponse(
    Long id,
    InvoiceType invoiceType, // TODO: Where to store this type?
    LocalDateTime dateTime
) {
}
