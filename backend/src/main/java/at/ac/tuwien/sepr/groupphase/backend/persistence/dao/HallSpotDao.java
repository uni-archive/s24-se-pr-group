package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSpotDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.HallSpotMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSpotRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HallSpotDao extends AbstractDao<HallSpot, HallSpotDto> {

    protected HallSpotDao(HallSpotRepository repository,
                          HallSpotMapper mapper) {
        super(repository, mapper);
    }

    public List<HallSpotDto> findByHallSectorId(Long hallSectorId) {
        return mapper.toDto(((HallSpotRepository) repository).findBySectorId(hallSectorId));
    }
}
