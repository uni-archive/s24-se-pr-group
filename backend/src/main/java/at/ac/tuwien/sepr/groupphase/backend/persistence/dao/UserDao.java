package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserDao extends AbstractDao<ApplicationUser, ApplicationUserDto> {

    private final UserSpecification userSpecification;

    public UserDao(UserRepository repository,
        UserMapper mapper, UserSpecification userSpecification) {
        super(repository, mapper);
        this.userSpecification = userSpecification;
    }


    public ApplicationUserDto findByEmail(String email) {
        return (mapper).toDto(((UserRepository) repository).findByEmail(email));
    }

    public Page<ApplicationUserDto> search(ApplicationUserSearchDto searchParameters) {
        Page<ApplicationUser> users = ((UserRepository) repository).findAll(
            userSpecification.getUserSpecification(searchParameters), searchParameters.pageable());
        return users.map(mapper::toDto);
    }
}
