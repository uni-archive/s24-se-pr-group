package at.ac.tuwien.sepr.groupphase.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.AbstractDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.impl.AbstractDtoImpl;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.impl.AbstractEntityImpl;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.AbstractServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.AbstractValidator;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class AbstractServiceTest {

    @Mock
    private AbstractValidator<AbstractDtoImpl> validator;

    @Mock
    private AbstractDao<AbstractEntityImpl, AbstractDtoImpl> dao;

    private AbstractService<AbstractDtoImpl> service;

    @BeforeEach
    void setUp() {
        service = new AbstractServiceImpl(validator, dao);
    }

    @Test
    void createShouldValidateBeforeReturningDaoResult() throws ForbiddenException, ValidationException {
        AbstractDtoImpl dto = new AbstractDtoImpl();
        when(dao.create(dto)).thenReturn(dto);

        AbstractDtoImpl result = service.create(dto);

        verify(validator).validateForCreate(dto);
        verify(dao).create(dto);
        assertEquals(dto, result);
    }

    @Test
    void createShouldPassValidationException() throws ValidationException {
        AbstractDtoImpl dto = new AbstractDtoImpl();
        doThrow(new ValidationException("Validation Error")).when(validator).validateForCreate(dto);

        ValidationException validationException = Assertions.assertThrows(ValidationException.class,
            () -> service.create(dto));

        verify(validator).validateForCreate(dto);
        assertEquals("Validation Error", validationException.getMessage());
    }

    @Test
    void updateShouldValidateBeforeReturningDaoResult()
        throws ValidationException, ForbiddenException, EntityNotFoundException {
        AbstractDtoImpl dto = new AbstractDtoImpl();
        when(dao.update(dto)).thenReturn(dto);

        AbstractDtoImpl result = service.update(dto);

        verify(validator).validateForUpdate(dto);
        verify(dao).update(dto);
        assertEquals(dto, result);
    }

    @Test
    void updateShouldPassValidationException() throws ValidationException {
        AbstractDtoImpl dto = new AbstractDtoImpl();
        doThrow(new ValidationException("Validation Error")).when(validator).validateForUpdate(dto);

        ValidationException validationException = Assertions.assertThrows(ValidationException.class,
            () -> service.update(dto));

        verify(validator).validateForUpdate(dto);
        assertEquals("Validation Error", validationException.getMessage());
    }

    @Test
    void findByIdShouldReturnDaoResult() throws EntityNotFoundException {
        AbstractDtoImpl dto = new AbstractDtoImpl();
        when(dao.findById(1L)).thenReturn(dto);

        AbstractDtoImpl result = service.findById(1L);

        verify(dao).findById(1L);
        assertEquals(dto, result);
    }

    @Test
    void deleteShouldCallDao() throws ForbiddenException, EntityNotFoundException {
        service.delete(1L);

        verify(dao).deleteById(1L);
    }

    @Test
    void findAllShouldReturnDaoResult() {
        AbstractDtoImpl dto = new AbstractDtoImpl();
        when(dao.findAll()).thenReturn(List.of(new AbstractDtoImpl[]{dto}));

        Iterable<AbstractDtoImpl> result = service.findAll();

        verify(dao).findAll();
        assertEquals(dto, result.iterator().next());
    }
}
