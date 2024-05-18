package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record ApplicationUserSearchDto(
    String firstName,
    String familyName,
    String email,
    boolean isLocked
) {
}
