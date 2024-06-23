package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSectorShow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HallSectorShowRepository extends JpaRepository<HallSectorShow, Long> {
    /**
     * Searches for all sectors that are used in a given show.
     *
     * @param showId the id of the show.
     * @return A list of all sectors used in a show.
     */
    List<HallSectorShow> findByShowId(long showId);

    /**
     * Gets the information for a (sector, show) tuple.
     *
     * @param showId the id of the show.
     * @param hallSectorId the id of the sector.
     * @return {@link HallSectorShow}
     *
     */
    @Query("SELECT hss FROM HallSectorShow hss INNER JOIN hss.show s INNER JOIN hss.sector sec WHERE s.id = :showId AND sec.id = :hallSectorId")
    List<HallSectorShow> findByShowIdAndSectorId(@Param("showId") long showId, @Param("hallSectorId") long hallSectorId);
}
