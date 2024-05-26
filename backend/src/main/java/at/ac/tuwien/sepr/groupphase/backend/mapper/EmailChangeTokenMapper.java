package at.ac.tuwien.sepr.groupphase.backend.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.EmailChangeTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EmailChangeToken;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface EmailChangeTokenMapper extends BaseEntityMapper<EmailChangeToken, EmailChangeTokenDto> {

    List<EmailChangeTokenDto> toDto(List<EmailChangeToken> emailChangeTokens);

    EmailChangeToken toEntity(EmailChangeTokenDto emailChangeTokenDto);

}
