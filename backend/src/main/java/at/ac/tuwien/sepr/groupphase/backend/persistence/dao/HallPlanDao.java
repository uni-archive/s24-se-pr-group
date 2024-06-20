package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallPlanDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.HallPlanMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallPlanRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class HallPlanDao extends AbstractDao<HallPlan, HallPlanDto> {

    protected HallPlanDao(HallPlanRepository repository,
                          HallPlanMapper mapper) {
        super(repository, mapper);
    }

    @Transactional
    public List<HallPlanDto> findByName(String name) {
        return mapper.toDto(((HallPlanRepository) repository).findHallPlansByNameContainingIgnoreCase(name, PageRequest.of(0, 10)));
    }
}
