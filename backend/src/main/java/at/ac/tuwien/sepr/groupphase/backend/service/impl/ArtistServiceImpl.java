package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ArtistSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.ArtistDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

@Service
public class ArtistServiceImpl implements ArtistService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistDao dao;

    public ArtistServiceImpl(ArtistDao dao) {
        this.dao = dao;
    }

    @Override
    public Page<ArtistDto> search(ArtistSearchDto searchDto) {
        if (Objects.isNull(searchDto.pageable())) {
            PageRequest defaultPage = PageRequest.of(0, 15, Sort.by("artistName", "firstName", "lastName"));
            return dao.searchArtists(searchDto.firstName(), searchDto.lastName(), searchDto.artistName(), defaultPage);
        }
        LOGGER.trace("Search artists by searchDto: {}", searchDto);
        return dao.searchArtists(searchDto.firstName(), searchDto.lastName(), searchDto.artistName(), searchDto.pageable());
    }

    @Override
    public ArtistDto findById(long artistId) throws DtoNotFoundException {
        try {
            return dao.findById(artistId);
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }
    }
}
