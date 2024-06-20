package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.AbstractDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.AbstractDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
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

    public T update(T dto) throws ValidationException, DtoNotFoundException, ForbiddenException {
        validator.validateForUpdate(dto);
        try {
            return dao.update(dto);
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }
    }

    public void delete(Long id) throws DtoNotFoundException, ForbiddenException {
        try {
            dao.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }
    }

    public T findById(Long id) throws DtoNotFoundException {
        try {
            return dao.findById(id);
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }
    }

    public Iterable<T> findAll() {
        return dao.findAll();
    }
}
