package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;

public record UserUpdateInfoRequest(long id, String email, String phoneNumber, AddressDto address) {
}
