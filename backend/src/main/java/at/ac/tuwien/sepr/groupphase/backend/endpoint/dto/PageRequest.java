package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.Optional;

public record PageRequest(Integer page, Integer size, Optional<String> sort) {

}
