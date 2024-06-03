package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.AbstractDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.AbstractEntity;
import org.mapstruct.Context;
import org.mapstruct.Named;

import java.util.List;

public interface BaseEntityMapper<T extends AbstractEntity, D extends AbstractDto> {

    T toEntity(D dto);

    List<T> toEntity(List<D> dto);

    @Named("toDtoWithoutContext")
    default D toDto(T entity) {
        return toDto(entity, new CycleAvoidingMappingContext());
    }

    @Named("toDtoListWithoutContext")
    default List<D> toDto(List<T> entity) {
        return toDto(entity, new CycleAvoidingMappingContext());
    }

    D toDto(T entity, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    List<D> toDto(List<T> entity, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);
}
