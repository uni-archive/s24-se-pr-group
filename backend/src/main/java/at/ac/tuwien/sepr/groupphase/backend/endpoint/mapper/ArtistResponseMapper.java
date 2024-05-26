package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistSearchResponse;
import org.mapstruct.Mapper;

@Mapper
public interface ArtistResponseMapper extends BaseResponseMapper<ArtistDto, ArtistSearchResponse> {
}
