package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record LocationCreateRequest(String name, AddressCreateRequest addressCreateRequest, Long hallPlanId) {
}
