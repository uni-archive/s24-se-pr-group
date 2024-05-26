package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSectorDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.HallSectorDao;
import at.ac.tuwien.sepr.groupphase.backend.service.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class HallSectorServiceImpl extends AbstractService<HallSectorDto> {
    HallSectorDao dao;
    public HallSectorServiceImpl(HallSectorDao dao) {
        super(null, dao);
        this.dao = dao;
    }

    @Override
    public HallSectorDto create(HallSectorDto dto) {
        return dao.create(dto);
    }
}
