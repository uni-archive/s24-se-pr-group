package at.ac.tuwien.sepr.groupphase.backend.dto;

public record TicketAddToOrderDto(Long spotId, Long orderId, Long showId, boolean reservationOnly) {
}
