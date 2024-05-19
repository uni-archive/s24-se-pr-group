package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = AddressResponseMapper.class, componentModel = "spring")
public interface LocationResponseMapper extends BaseResponseMapper<LocationDto, LocationResponse> {

    @Mapping(source = "addressCreateRequest", target = "address")
    LocationDto createRequestToDto(LocationCreateRequest createRequest);
}
