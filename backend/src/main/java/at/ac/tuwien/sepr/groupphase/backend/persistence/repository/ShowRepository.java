package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    @EntityGraph(attributePaths = {"artists", "event"})
    Page<Show> findByLocationId(Long locationId, Pageable pageable);

    @Query("Select s from Show s join HallSectorShow h on (s.id = h.show.id) "
        + "where ((CAST(s.dateTime as date) = CAST(?1 as date) "
        + "and s.dateTime>?1) OR ?1 is null) "
        + "and ((s.event.id = ?3) or (?3 = 0 )) "
        + "group by s.id "
        + "having (min(h.price) < (?2 * 1.3) and min(h.price) > (?2 * 0.7)) OR ?2 = 0")
    List<Show> findShowsBySearchDto(LocalDateTime dateTime, Long maxPrice, Long eventId);

    @Query("Select coalesce(MIN(h.price), 0) from Show s join HallSectorShow h on (s.id = h.show.id)"
        +   "where s.id = ?1 "
        +   "group by s.id")
    Long getStartingPriceOfShow(long id);

    @Query("SELECT s from Show s join HallSectorShow h on (s.id = h.show.id) where s.event.id = ?1")
    List<Show> getShowByEventId(long eventid);

    @Query("SELECT s from Show s")
    List<Show> getAllShows();

}
