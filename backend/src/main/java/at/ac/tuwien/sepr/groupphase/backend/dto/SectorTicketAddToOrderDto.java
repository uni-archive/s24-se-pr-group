package at.ac.tuwien.sepr.groupphase.backend.dto;

public record SectorTicketAddToOrderDto(Long sectorId, Long orderId, Long showId, boolean reservationOnly) {
}
