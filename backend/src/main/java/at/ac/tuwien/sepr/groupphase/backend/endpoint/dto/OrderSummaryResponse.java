package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderSummaryResponse(
    Long id,
    int ticketCount,
    long totalPrice,
    List<InvoiceResponse> invoices,

    LocalDateTime dateTime
) {
}
