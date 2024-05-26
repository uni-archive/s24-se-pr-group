package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistSearchResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ArtistResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/api/v1/artists")
public class ArtistEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistService artistService;
    private final ArtistResponseMapper artistResponseMapper;

    public ArtistEndpoint(ArtistService artistService, ArtistResponseMapper artistResponseMapper) {
        this.artistService = artistService;
        this.artistResponseMapper = artistResponseMapper;
    }

    @Secured("ROLE_USER")
    @PostMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ArtistSearchResponse>> search(@ModelAttribute ArtistSearchDto searchDto) {
        return ResponseEntity.ok(artistResponseMapper.toResponseList(artistService.search(searchDto)));
    }
}
