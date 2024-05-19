package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.AbstractDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.impl.AbstractDtoImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.AbstractService;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.AbstractValidator;

public class AbstractServiceImpl extends AbstractService<AbstractDtoImpl> {

    public AbstractServiceImpl(
        AbstractValidator<AbstractDtoImpl> validator,
        AbstractDao<?, AbstractDtoImpl> dao) {
        super(validator, dao);
    }
}
