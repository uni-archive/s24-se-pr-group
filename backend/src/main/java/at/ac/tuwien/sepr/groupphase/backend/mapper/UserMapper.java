package at.ac.tuwien.sepr.groupphase.backend.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.BaseResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper extends BaseEntityMapper<ApplicationUser, ApplicationUserDto>,
    BaseResponseMapper<ApplicationUserDto, ApplicationUserResponse> {

    ApplicationUserDto toDto(ApplicationUser user);

    ApplicationUserDto toDto(UserCreateRequest createRequest);
}
