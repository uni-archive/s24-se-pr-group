package at.ac.tuwien.sepr.groupphase.backend.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.AbstractDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.AbstractEntity;
import java.util.List;

public interface BaseEntityMapper<T extends AbstractEntity, D extends AbstractDto> {

    T toEntity(D dto);

    D toDto(T entity);

    List<T> toEntity(List<D> dto);

    List<D> toDto(List<T> entity);
}
