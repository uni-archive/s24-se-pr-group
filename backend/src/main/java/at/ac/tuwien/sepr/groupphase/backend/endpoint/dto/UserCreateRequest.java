package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record UserCreateRequest(String email, String password, String firstName, String familyName, String phoneNumber,
                                boolean isAdmin, AddressCreateRequest addressCreateRequest) {

}
