package at.ac.tuwien.sepr.groupphase.backend.persistence.dao.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.AbstractDto;

public class AbstractDtoImpl implements AbstractDto {

    Long id;
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
