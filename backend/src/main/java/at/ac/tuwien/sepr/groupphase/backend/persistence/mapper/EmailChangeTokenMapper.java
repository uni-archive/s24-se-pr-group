package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.EmailChangeTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EmailChangeToken;
import org.mapstruct.Mapper;

@Mapper
public interface EmailChangeTokenMapper extends BaseEntityMapper<EmailChangeToken, EmailChangeTokenDto> {
}
