package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.AbstractDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PageRequest;
import java.util.List;
import org.mapstruct.Named;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface BaseResponseMapper<D extends AbstractDto, R> {

    D toDto(R response);

    R toResponse(D dto);

    List<D> toDtoList(List<R> responseList);

    List<R> toResponseList(List<D> dtoList);
}
