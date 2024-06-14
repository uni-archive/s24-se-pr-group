package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.NewPasswordTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.NewPasswordToken;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.BaseEntityMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.NewPasswordTokenRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewPasswordTokenDao extends AbstractDao<NewPasswordToken, NewPasswordTokenDto> {
    protected NewPasswordTokenDao(JpaRepository<NewPasswordToken, Long> repository, BaseEntityMapper<NewPasswordToken, NewPasswordTokenDto> mapper) {
        super(repository, mapper);
    }

    public NewPasswordTokenDto findByToken(String token) {
        return (mapper).toDto(((NewPasswordTokenRepository) repository).findByToken(token));
    }

    public List<NewPasswordTokenDto> findByEmail(String email) {
        return (mapper).toDto(((NewPasswordTokenRepository) repository).findByEmail(email));
    }
}
