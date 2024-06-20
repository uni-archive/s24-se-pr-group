package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallPlanDto;

public record LocationResponse(Long id, String name, AddressResponse address, HallPlanDto hallPlan) {

}
