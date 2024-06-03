package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateInfoRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {AddressResponseMapper.class})
public interface ApplicationUserMapper extends BaseResponseMapper<ApplicationUserDto, ApplicationUserResponse> {

    @Mapping(source = "addressCreateRequest", target = "address")
    ApplicationUserDto toDto(UserCreateRequest createRequest);

    @Override
    @Mapping(source = "admin", target = "isAdmin")
    ApplicationUserResponse toResponse(ApplicationUserDto dto);

    ApplicationUserDto toDto(UserUpdateInfoRequest updateRequest);
}
