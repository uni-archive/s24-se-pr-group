package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    @Query("SELECT a FROM Artist a"
            + " WHERE (:firstName is null OR UPPER(a.firstName) LIKE UPPER('%' || :firstName || '%'))"
              + " AND (:lastName is null OR UPPER(a.lastName) LIKE UPPER('%' || :lastName || '%'))"
              + " AND (:artistName is null OR UPPER(a.artistName) LIKE UPPER('%' || :artistName || '%'))"
        )
    Page<Artist> search(@Param("firstName") String firstname, @Param("lastName") String lastname, @Param("artistName") String artistName, Pageable pageable);

    @Query("Select distinct a from Artist a join a.shows show where show.id = ?1")
    List<Artist> findByShowId(long showId);
}
