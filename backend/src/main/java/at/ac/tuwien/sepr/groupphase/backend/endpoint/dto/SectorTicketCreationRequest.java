package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record SectorTicketCreationRequest(Long sectorId, Long orderId, Long showId, boolean reservationOnly) {


}
