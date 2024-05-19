package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailsResponse(
    Long id,
    List<TicketDetailsResponse> tickets,
    ApplicationUserResponse customer,
    List<InvoiceResponse> invoices,
    LocalDateTime dateTime
) {
}
