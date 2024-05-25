package at.ac.tuwien.sepr.groupphase.backend.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateInfoRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.BaseResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.UserType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface UserMapper extends BaseEntityMapper<ApplicationUser, ApplicationUserDto>,
    BaseResponseMapper<ApplicationUserDto, ApplicationUserResponse> {

    @Named("booleanToUserType")
    static UserType booleanToUserType(boolean isAdmin) {
        return isAdmin ? UserType.ADMIN : UserType.CUSTOMER;
    }

    ApplicationUserDto toDto(ApplicationUser user);

    ApplicationUserDto toDto(UserCreateRequest createRequest);

    ApplicationUserDto toDto(UserUpdateInfoRequest updateRequest);

    @Override
    @Mapping(source = "admin", target = "type", qualifiedByName = "booleanToUserType")
    ApplicationUser toEntity(ApplicationUserDto dto);
}
