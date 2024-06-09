package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan;

public record ShowHallplanSpotResponse(
    Long id,

    boolean isReserved,

    String frontendCoordinates) {
}
