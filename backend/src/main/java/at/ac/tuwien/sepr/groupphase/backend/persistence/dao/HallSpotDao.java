package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSeatDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.HallSpotDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSeat;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.HallSpotMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSpotRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class HallSpotDao extends AbstractDao<HallSpot, HallSpotDto> {

    protected HallSpotDao(HallSpotRepository repository,
        HallSpotMapper mapper) {
        super(repository, mapper);
    }

    public List<HallSpotDto> findByHallSectorId(Long hallSectorId) {
        return mapper.toDto(((HallSpotRepository) repository).findBySectorId(hallSectorId));
    }

    public HallSpotDto findById(Long spotId){
        Optional<HallSpot> spot = ((HallSpotRepository) repository).findById(spotId);
        if (spot.isEmpty()) {
            throw new EntityNotFoundException("HallSpot with id " + spotId + " not found");
        }
        return mapper.toDto(spot.get());
    }

    public boolean existsById(Long spotId) {
        return ((HallSpotRepository) repository).existsById(spotId);
    }
}
