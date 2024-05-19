package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record ApplicationUserResponse(
    Long id,
    String email,
    String firstName,
    String familyName,
    String phoneNumber,
    boolean accountLocked,
    boolean isAdmin,
    boolean isSuperAdmin
) {
}
