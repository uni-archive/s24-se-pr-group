package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ArtistSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import org.springframework.data.domain.Page;

public interface ArtistService {
    /**
     * Searches for artists by the given search-criteria in {@link ArtistSearchDto}.
     * If one of the provided fields match then the artist is returned.
     *
     * @param searchDto the search criteria.
     * @return A list of all artists that match any search field given in {@code searchDto}
     */
    Page<ArtistDto> search(ArtistSearchDto searchDto);

    ArtistDto findById(long artistId) throws DtoNotFoundException;
}
