package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record HallSectorShowResponse(
    Long id,
    ShowResponse show,
    HallSectorResponse sector,
    long price
) {
}
