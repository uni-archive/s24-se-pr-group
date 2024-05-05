package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserDao extends AbstractDao<ApplicationUser, ApplicationUserDto> {

    public UserDao(UserRepository repository,
        UserMapper mapper) {
        super(repository, mapper);
    }

    public ApplicationUserDto findByEmail(String email) {
        return (mapper).toDto(((UserRepository)repository).findByEmail(email));
    }
}
