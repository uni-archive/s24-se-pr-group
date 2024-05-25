package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.AbstractDto;
import at.ac.tuwien.sepr.groupphase.backend.mapper.BaseEntityMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.AbstractEntity;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class AbstractDao<T extends AbstractEntity, D extends AbstractDto> {

    protected final JpaRepository<T, Long> repository;
    protected final BaseEntityMapper<T, D> mapper;


    protected AbstractDao(JpaRepository<T, Long> repository, BaseEntityMapper<T, D> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public D create(D createDto) {
        return mapper.toDto(repository.save(mapper.toEntity(createDto)));
    }

    public D findById(Long id) throws EntityNotFoundException {
        return mapper.toDto(repository.findById(id).orElseThrow(() -> new EntityNotFoundException(id)));
    }

    public void deleteById(Long id) throws EntityNotFoundException {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(id);
        }
        repository.deleteById(id);
    }

    public D update(D entity) throws EntityNotFoundException {
        if (!repository.existsById(entity.getId())) {
            throw new EntityNotFoundException(entity.getId());
        }
        return mapper.toDto(repository.save(mapper.toEntity(entity)));
    }

    public List<D> findAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }
}
