package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSpotDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.HallSpotMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSpotRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class HallSpotDao extends AbstractDao<HallSpot, HallSpotDto> {

    protected HallSpotDao(HallSpotRepository repository,
                          HallSpotMapper mapper) {
        super(repository, mapper);
    }

    public List<HallSpotDto> findByHallSectorId(Long hallSectorId) {
        return mapper.toDto(((HallSpotRepository) repository).findBySectorId(hallSectorId));
    }

    public HallSpotDto findById(Long spotId) throws EntityNotFoundException {
        Optional<HallSpot> spot = repository.findById(spotId);
        if (spot.isEmpty()) {
            throw new EntityNotFoundException(spotId);
        }
        return mapper.toDto(spot.get());
    }

    public boolean existsById(Long spotId) {
        return repository.existsById(spotId);
    }
}
