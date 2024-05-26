package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Artist;
import org.h2.command.query.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    @Query("SELECT a FROM Artist a"
            + " WHERE (:#{#searchDto.firstName} is null OR UPPER(a.firstName) LIKE UPPER('%' || :#{#searchDto.firstName} || '%'))"
              + " AND (:#{#searchDto.lastName} is null OR UPPER(a.lastName) LIKE UPPER('%' || :#{#searchDto.lastName} || '%'))"
              + " AND (:#{#searchDto.artistName} is null OR UPPER(a.artistName) LIKE UPPER('%' || :#{#searchDto.artistName} || '%'))"
        )
    List<Artist> search(@Param("searchDto") ArtistSearchDto searchDto);

    @Query("Select distinct a from Artist a join a.shows show where show.id = ?1")
    List<Artist> findByShowId(long showId);
}
