package at.ac.tuwien.sepr.groupphase.backend.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.AbstractDto;
import java.util.List;

public interface BaseResponseMapper<D extends AbstractDto, R> {

    D toDto(R response);

    R toResponse(D dto);

    List<D> toDtoList(List<R> responseList);

    List<R> toResponseList(List<D> dtoList);
}
