package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSectorShowDto;
import at.ac.tuwien.sepr.groupphase.backend.mapper.HallSectorShowMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSectorShow;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorShowRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class HallSectorShowDao extends AbstractDao<HallSectorShow, HallSectorShowDto> {
    public HallSectorShowDao(HallSectorShowRepository repository, HallSectorShowMapper mapper) {
        super(repository, mapper);
    }

    @Transactional
    public List<HallSectorShowDto> findByShowId(long id) {
        var found = ((HallSectorShowRepository) repository).findByShowId(id);
        return found.stream().map(mapper::toDto).toList();
    }

    @Transactional
    public HallSectorShowDto findByShowIdAndSectorId(long showId, long hallSectorId) throws EntityNotFoundException {
        var opt = ((HallSectorShowRepository) repository).findByShowIdAndSectorId(showId, hallSectorId);
        var found = opt.orElseThrow(() -> new EntityNotFoundException(showId)); // TODO: need more params
        return mapper.toDto(found);
    }
}
