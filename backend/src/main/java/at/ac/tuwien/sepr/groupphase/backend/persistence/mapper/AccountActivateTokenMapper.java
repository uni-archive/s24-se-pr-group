package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.AccountActivateTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.AccountActivateToken;
import org.mapstruct.Mapper;

@Mapper
public interface AccountActivateTokenMapper extends BaseEntityMapper<AccountActivateToken, AccountActivateTokenDto> {
}
