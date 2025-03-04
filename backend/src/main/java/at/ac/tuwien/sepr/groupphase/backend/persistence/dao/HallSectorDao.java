package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSectorDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.HallSectorMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSpotRepository;
import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class HallSectorDao extends AbstractDao<HallSector, HallSectorDto> {


    protected HallSectorDao(HallSectorRepository repository,
        HallSectorMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public HallSectorDto create(HallSectorDto createDto) {
        return super.create(createDto);
    }

    @Transactional
    public List<HallSectorDto> findByHallPlanId(Long hallPlanId) {
        return mapper.toDto(((HallSectorRepository) repository).findByHallPlanId(hallPlanId));
    }

}
