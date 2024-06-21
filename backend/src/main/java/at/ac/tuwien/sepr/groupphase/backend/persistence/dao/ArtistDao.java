package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.BaseEntityMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ArtistRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class ArtistDao extends AbstractDao<Artist, ArtistDto>  {
    protected ArtistDao(JpaRepository<Artist, Long> repository,
                        BaseEntityMapper<Artist, ArtistDto> mapper) {
        super(repository, mapper);
    }

    @Transactional
    public Page<ArtistDto> searchArtists(String firstName, String lastName, String artistName, Pageable pageable) {
        return ((ArtistRepository) repository).search(firstName, lastName, artistName, pageable)
            .map(mapper::toDto);
    }
}
