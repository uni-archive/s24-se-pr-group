package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.AccountActivateTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.AccountActivateToken;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.AccountActivateTokenMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AccountActivateTokenRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountActivateTokenDao extends AbstractDao<AccountActivateToken, AccountActivateTokenDto> {
    protected AccountActivateTokenDao(AccountActivateTokenRepository repository, AccountActivateTokenMapper mapper) {
        super(repository, mapper);
    }

    public AccountActivateTokenDto findByToken(String token) {
        return (mapper).toDto(((AccountActivateTokenRepository) repository).findByToken(token));
    }

    public List<AccountActivateTokenDto> findByEmail(String email) {
        return (mapper).toDto(((AccountActivateTokenRepository) repository).findByEmail(email));
    }
}
