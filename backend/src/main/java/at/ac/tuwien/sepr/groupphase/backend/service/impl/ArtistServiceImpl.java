package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.ArtistDao;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class ArtistServiceImpl implements ArtistService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistDao dao;

    public ArtistServiceImpl(ArtistDao dao) {
        this.dao = dao;
    }

    @Override
    public List<ArtistDto> search(ArtistSearchDto searchDto) {
        LOGGER.trace("Search artists by searchDto: {}", searchDto);
        return dao.searchArtists(searchDto);
    }
}
