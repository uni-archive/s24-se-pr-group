package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistSearchDto;

import java.util.List;

public interface ArtistService {
    /**
     * Searches for artists by the given search-criteria in {@link ArtistSearchDto}.
     * If one of the provided fields match then the artist is returned.
     *
     * @param searchDto the search criteria.
     * @return A list of all artists that match any search field given in {@code searchDto}
     */
    List<ArtistDto> search(ArtistSearchDto searchDto);
}
