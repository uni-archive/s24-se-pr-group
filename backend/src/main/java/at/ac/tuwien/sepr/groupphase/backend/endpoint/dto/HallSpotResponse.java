package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record HallSpotResponse(
    Long id,
    HallSectorResponse sector
) {
}
