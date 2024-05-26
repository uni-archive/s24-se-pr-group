package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallPlanDto;
import at.ac.tuwien.sepr.groupphase.backend.mapper.HallPlanMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallPlanRepository;
import org.springframework.stereotype.Component;

@Component
public class HallPlanDao extends AbstractDao<HallPlan, HallPlanDto> {

    protected HallPlanDao(HallPlanRepository repository,
                          HallPlanMapper mapper) {
        super(repository, mapper);
    }
}
