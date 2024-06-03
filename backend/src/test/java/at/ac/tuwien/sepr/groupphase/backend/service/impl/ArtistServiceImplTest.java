package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.ArtistMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ArtistRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
@SpringBootTest
@ActiveProfiles("test")
public class ArtistServiceImplTest {
    @Autowired
    private ArtistServiceImpl artistService;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistMapper artistMapper;

    private static List<Artist> generatedArtists;
    private static Map<Long, ArtistDto> artistById;

    private static final Faker faker = new Faker();

    private static Artist generateArtist() {
        var artist = new Artist();
        artist.setArtistName(faker.artist().name());
        artist.setFirstName(faker.name().firstName());
        artist.setLastName(faker.name().lastName());
        return artist;
    }

    private static List<Artist> generateArtists(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> generateArtist())
            .collect(Collectors.toList());
    }

    @BeforeEach
    void setup() {
        generatedArtists = generateArtists(100);
        artistRepository.saveAll(generatedArtists);

        artistById = artistMapper.toDto(generatedArtists).stream()
            .collect(Collectors.toMap(
                ArtistDto::getId,
                Function.identity()
            ));
    }

    @AfterEach
    void teardown() {
        artistRepository.deleteAll();
    }

    @Test
    void searching_artistsWithExactMatches_returnsTheOnlyArtistMatchingAllFields() {
        for (var a : generatedArtists) {
            var searchObj = new ArtistSearchDto(a.getFirstName(), a.getLastName(), a.getArtistName());
            var found = artistService.search(searchObj);

            assertThat(found)
                .hasSize(1);

            var foundArtist = found.get(0);

            var expected = artistById.get(foundArtist.getId());
            assertThat(foundArtist)
                .usingRecursiveComparison()
                .isEqualTo(expected);
        }
    }

    @Test
    void searching_artistByNonExistentName_returnsEmptyList() {
        var noFirstnameMatch = new ArtistSearchDto("0123456789", null, null);
        assertThat(artistService.search(noFirstnameMatch))
            .hasSize(0);

        var noLastnameMatch = new ArtistSearchDto(null, "0123456789", null);
        assertThat(artistService.search(noLastnameMatch))
            .hasSize(0);

        var noArtistNameMatch = new ArtistSearchDto(null, null, "0123456789");
        assertThat(artistService.search(noArtistNameMatch))
            .hasSize(0);
    }

    @Test
    void searching_WithAllSearchParamsEqualToNull_returnsAllArtists() {
        assertThat(artistService.search(new ArtistSearchDto(null, null, null)))
            .hasSize(generatedArtists.size())
            .containsExactlyInAnyOrder(artistById.values().toArray(ArtistDto[]::new))
            .usingRecursiveComparison();
    }

    @Test
    void searching_WithAllSearchParamsEmpty_returnsAllArtists() {
        assertThat(artistService.search(new ArtistSearchDto("", "", "")))
            .hasSize(generatedArtists.size())
            .containsExactlyInAnyOrder(artistById.values().toArray(ArtistDto[]::new))
            .usingRecursiveComparison();
    }

    @Test
    void searching_WithSomeSearchParamsEqualToNull_returnsAnonEmptySubsetOfAllArtists() {
        var randomArtist = generatedArtists.get(new Random().nextInt(0, generatedArtists.size()));
        var firstnameSet = new ArtistSearchDto(randomArtist.getFirstName(), null, null);
        assertThat(artistService.search(firstnameSet))
            .allSatisfy(artist ->
                assertThat(artist)
                    .isNotNull()
                    .isEqualTo(artistById.get(artist.getId()))
                    .usingRecursiveComparison()
            )
            .size()
            .isStrictlyBetween(0, generatedArtists.size());

        var lastnameSet = new ArtistSearchDto(null, randomArtist.getLastName(), null);
        assertThat(artistService.search(lastnameSet))
            .allSatisfy(artist ->
                assertThat(artist)
                    .isNotNull()
                    .isEqualTo(artistById.get(artist.getId()))
                    .usingRecursiveComparison()
            )
            .size()
            .isStrictlyBetween(0, generatedArtists.size());

        var artistNameSet = new ArtistSearchDto(null, null, randomArtist.getArtistName());
        assertThat(artistService.search(artistNameSet))
            .allSatisfy(artist ->
                assertThat(artist)
                    .isNotNull()
                    .isEqualTo(artistById.get(artist.getId()))
                    .usingRecursiveComparison()
            )
            .size()
            .isStrictlyBetween(0, generatedArtists.size());
    }
}
