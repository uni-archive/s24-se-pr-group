package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.mapper.BaseEntityMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.impl.AbstractDaoImpl;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.impl.AbstractDtoImpl;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.impl.AbstractEntityImpl;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractDaoTest {

    @Mock
    private JpaRepository<AbstractEntityImpl, Long> repository;

    @Mock
    private BaseEntityMapper<AbstractEntityImpl, AbstractDtoImpl> mapper;

    private AbstractDao<AbstractEntityImpl, AbstractDtoImpl> abstractDao;

    @BeforeEach
    void setUp() {
        abstractDao = new AbstractDaoImpl(repository, mapper);
    }

    @Test
    void createShouldCallRepositoryAndMapper() {
        AbstractDtoImpl dto = new AbstractDtoImpl();
        AbstractEntityImpl entity = new AbstractEntityImpl();
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        AbstractDtoImpl result = abstractDao.create(dto);

        verify(repository).save(entity);
        verify(mapper).toEntity(dto);
        verify(mapper).toDto(entity);
        assertEquals(dto, result);
    }

    @Test
    void findByIdShouldCallRepositoryAndMapper() throws EntityNotFoundException {
        AbstractEntityImpl entity = new AbstractEntityImpl();
        AbstractDtoImpl dto = new AbstractDtoImpl();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        AbstractDtoImpl result = abstractDao.findById(1L);

        verify(repository).findById(1L);
        verify(mapper).toDto(entity);
        assertEquals(dto, result);
    }

    @Test
    void findByIdShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> abstractDao.findById(1L));
    }

    @Test
    void deleteByIdShouldCallRepository() throws EntityNotFoundException {
        when(repository.existsById(1L)).thenReturn(true);

        abstractDao.deleteById(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteByIdShouldThrowNotFoundException() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> abstractDao.deleteById(1L));
    }

    @Test
    void updateShouldCallRepositoryAndMapper() throws EntityNotFoundException {
        AbstractDtoImpl dto = new AbstractDtoImpl();
        dto.setId(1L);
        AbstractEntityImpl entity = new AbstractEntityImpl();
        when(repository.existsById(1L)).thenReturn(true);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        AbstractDtoImpl result = abstractDao.update(dto);

        verify(repository).existsById(1L);
        verify(repository).save(entity);
        verify(mapper).toEntity(dto);
        verify(mapper).toDto(entity);
        assertEquals(dto, result);
    }

    @Test
    void updateShouldThrowNotFoundException() {
        AbstractDtoImpl dto = new AbstractDtoImpl();
        dto.setId(1L);
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> abstractDao.update(dto));
    }

    @Test
    void findAllShouldCallRepositoryAndMapper() {
        AbstractEntityImpl entity1 = new AbstractEntityImpl();
        AbstractEntityImpl entity2 = new AbstractEntityImpl();
        AbstractDtoImpl dto1 = new AbstractDtoImpl();
        AbstractDtoImpl dto2 = new AbstractDtoImpl();
        when(repository.findAll()).thenReturn(Arrays.asList(entity1, entity2));
        when(mapper.toDto(entity1)).thenReturn(dto1);
        when(mapper.toDto(entity2)).thenReturn(dto2);

        List<AbstractDtoImpl> result = abstractDao.findAll();

        verify(repository).findAll();
        verify(mapper).toDto(entity1);
        verify(mapper).toDto(entity2);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }
}
