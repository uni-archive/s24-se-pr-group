package at.ac.tuwien.sepr.groupphase.backend.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.AddressResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.BaseResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.UserType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.Named;

@Mapper(uses = {AddressMapper.class, AddressResponseMapper.class}, componentModel = ComponentModel.SPRING)
public interface UserMapper extends BaseEntityMapper<ApplicationUser, ApplicationUserDto>,
    BaseResponseMapper<ApplicationUserDto, ApplicationUserResponse> {

    @Named("booleanToUserType")
    static UserType booleanToUserType(boolean isAdmin) {
        return isAdmin ? UserType.ADMIN : UserType.CUSTOMER;
    }

    @Mapping(source = "addressCreateRequest", target = "address")
    ApplicationUserDto toDto(UserCreateRequest createRequest);

    @Override
    @Mapping(source = "admin", target = "type", qualifiedByName = "booleanToUserType")
    ApplicationUser toEntity(ApplicationUserDto dto);

    @Named("booleanToUserType")
    static UserType booleanToUserType(boolean isAdmin) {
        return isAdmin ? UserType.ADMIN : UserType.CUSTOMER;
    }
}
