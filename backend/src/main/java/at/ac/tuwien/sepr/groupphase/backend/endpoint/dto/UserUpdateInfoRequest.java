package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record UserUpdateInfoRequest(long id, String email, String phoneNumber) {
}
