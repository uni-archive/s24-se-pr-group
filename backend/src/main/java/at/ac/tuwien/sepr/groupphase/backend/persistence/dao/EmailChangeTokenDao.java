package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.EmailChangeTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.mapper.EmailChangeTokenMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EmailChangeToken;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EmailChangeTokenRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailChangeTokenDao extends AbstractDao<EmailChangeToken, EmailChangeTokenDto> {
    public EmailChangeTokenDao(EmailChangeTokenRepository repository, EmailChangeTokenMapper mapper) {
        super(repository, mapper);
    }

    public List<EmailChangeTokenDto> findByCurrentEmail(String currentEmail) {
        return (mapper).toDto(((EmailChangeTokenRepository) repository).findByCurrentEmail(currentEmail));
    }
}
