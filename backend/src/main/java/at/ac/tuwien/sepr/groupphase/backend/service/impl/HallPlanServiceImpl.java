package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallPlanDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.HallPlanDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.HallSectorDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.HallSpotDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;

@Service
public class HallPlanServiceImpl extends AbstractService<HallPlanDto> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    HallPlanDao dao;
    HallSectorDao hallSectorDao;
    HallSpotDao hallSpotDao;

    public HallPlanServiceImpl(HallPlanDao dao, HallSectorDao hallSectorDao, HallSpotDao hallSpotDao) {
        super(null, dao);
        this.dao = dao;
        this.hallSectorDao = hallSectorDao;
        this.hallSpotDao = hallSpotDao;
    }

    @Transactional
    @Override
    public HallPlanDto create(HallPlanDto dto) {
        var plan = dao.create(dto.withoutSectors());
        dto.getSectors().forEach(section -> {
            section.setHallPlan(plan);
            var sectionInDb = hallSectorDao.create(section.withoutSeats());
            section.getSeats().forEach(seat -> {
                seat.setSector(sectionInDb);
                hallSpotDao.create(seat);
            });
        });
        HallPlanDto retVal = null;
        try {
            retVal = dao.findById(plan.getId());
            retVal.setSectors(hallSectorDao.findByHallPlanId(plan.getId()));
            retVal.getSectors().forEach(sector -> {
                sector.setSeats(hallSpotDao.findByHallSectorId(sector.getId()));
            });
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
        return retVal;
    }
}
