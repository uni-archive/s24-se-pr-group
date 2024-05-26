package at.ac.tuwien.sepr.groupphase.backend.mapper;


import at.ac.tuwien.sepr.groupphase.backend.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Artist;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ArtistMapper extends BaseEntityMapper<Artist, ArtistDto> {
}
