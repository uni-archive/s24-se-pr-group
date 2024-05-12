package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record TicketDetailsResponse(
    Long id,
    String hash,
    boolean reserved,
    boolean valid,
    HallSpotResponse hallSpot,
    ShowResponse show
) {
}
