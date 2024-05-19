package at.ac.tuwien.sepr.groupphase.backend.persistence.dao.impl;

import at.ac.tuwien.sepr.groupphase.backend.mapper.BaseEntityMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.AbstractDao;
import org.mockito.Mock;
import org.springframework.data.jpa.repository.JpaRepository;

public class AbstractDaoImpl extends AbstractDao<AbstractEntityImpl, AbstractDtoImpl> {

    public AbstractDaoImpl(JpaRepository<AbstractEntityImpl, Long> repository, BaseEntityMapper<AbstractEntityImpl, AbstractDtoImpl> mapper) {
        super(repository, mapper);
    }
}
