package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record TicketCreationRequest(Long spotId, Long orderId, Long showId, boolean reservationOnly) {


}
