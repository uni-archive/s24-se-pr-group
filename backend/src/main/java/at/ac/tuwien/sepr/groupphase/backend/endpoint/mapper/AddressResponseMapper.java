package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AddressCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AddressResponse;
import org.mapstruct.Mapper;

@Mapper
public interface AddressResponseMapper extends BaseResponseMapper<AddressDto, AddressResponse> {

    AddressDto createRequestToDto(AddressCreateRequest createRequest);

}
