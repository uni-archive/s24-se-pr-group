package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.mapper.BaseEntityMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ArtistRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArtistDao extends AbstractDao<Artist, ArtistDto>  {
    protected ArtistDao(JpaRepository<Artist, Long> repository,
                        BaseEntityMapper<Artist, ArtistDto> mapper) {
        super(repository, mapper);
    }

    @Transactional
    public List<ArtistDto> searchArtists(ArtistSearchDto searchDto) {
        return mapper.toDto(((ArtistRepository) repository).search(searchDto));
    }
}
