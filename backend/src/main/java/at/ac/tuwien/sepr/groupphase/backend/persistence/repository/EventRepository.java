package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventWithTicketCountProjection;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;
import org.h2.command.query.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("Select e from Event e "
        + "where ((e.duration < (?1 * 1.3) and e.duration > (?1 * 0.7)) or (?1 = 0))"
        + "and ((e.eventType = ?2) or (?2 is null))"
        + "and ((UPPER(e.title) like '%' || UPPER(?3) || '%') or (UPPER(e.description) like '%' || UPPER(?3) || '%') or (?3 = '') or (?3 is null)) ")
    Page<Event> findBySearchDto(long duration, EventType type, String textSearch, Pageable pageable);

    @Query("Select e from Event e")
    List<Event> getAllEvents();

    @Query("SELECT e from Event e where e.id = ?1")
    Event findById(long id);

    @Query("SELECT s.event FROM Artist a INNER JOIN a.shows s WHERE a.id = :artistId")
    List<Event> findByArtist(@Param("artistId") long artistId);

    @Query("SELECT e as event, COUNT(t) as ticketCount "
        + "FROM Ticket t JOIN t.show s JOIN s.event e "
        + "WHERE s.dateTime >= :startDate "
        + "GROUP BY e.id "
        + "ORDER BY COUNT(t) DESC")
    List<EventWithTicketCountProjection> findTop10ByOrderByTicketCountDesc(
        @Param("startDate") LocalDateTime startDate,
        Pageable pageable);
}
