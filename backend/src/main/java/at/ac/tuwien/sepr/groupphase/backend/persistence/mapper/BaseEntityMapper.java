package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.AbstractDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.AbstractEntity;
import org.mapstruct.Context;
import org.mapstruct.Named;

import java.util.List;

public interface BaseEntityMapper<T extends AbstractEntity, D extends AbstractDto> {

    @Named("toEntityWithoutContext")
    default T toEntity(D dto) { return toEntity(dto, new CycleAvoidingMappingContext());}

    @Named("toEntityWithoutContext")
    default List<T> toEntity(List<D> dto) {
        return toEntity(dto, new CycleAvoidingMappingContext());
    }

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

    T toEntity(D dto, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    List<T> toEntity(List<D> dto, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);
}
