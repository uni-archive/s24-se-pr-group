package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.NewPasswordTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.NewPasswordToken;
import org.mapstruct.Mapper;

@Mapper
public interface NewPasswordTokenMapper extends BaseEntityMapper<NewPasswordToken, NewPasswordTokenDto> {
}
