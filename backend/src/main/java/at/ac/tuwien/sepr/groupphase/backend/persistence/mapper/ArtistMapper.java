package at.ac.tuwien.sepr.groupphase.backend.persistence.mapper;


import at.ac.tuwien.sepr.groupphase.backend.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Artist;
import org.mapstruct.Mapper;

@Mapper
public interface ArtistMapper extends BaseEntityMapper<Artist, ArtistDto> {
}
