package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.AbstractDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.AbstractDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.AbstractValidator;

public abstract class AbstractService<T extends AbstractDto> {

    private final AbstractValidator<T> validator;
    private final AbstractDao<?, T> dao;

    public AbstractService(AbstractValidator<T> validator, AbstractDao<?, T> dao) {
        this.validator = validator;
        this.dao = dao;
    }

    public T create(T dto) throws ValidationException, ForbiddenException {
        validator.validateForCreate(dto);
        return dao.create(dto);
    }

    public T update(T dto) throws ValidationException, EntityNotFoundException, ForbiddenException {
        validator.validateForUpdate(dto);
        return dao.update(dto);
    }

    public void delete(Long id) throws EntityNotFoundException, ForbiddenException {
        dao.deleteById(id);
    }

    public T findById(Long id) throws EntityNotFoundException {
        return dao.findById(id);
    }

    public Iterable<T> findAll() {
        return dao.findAll();
    }
}
