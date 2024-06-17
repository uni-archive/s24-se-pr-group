package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import org.springframework.data.domain.Page;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t FROM Ticket t INNER JOIN t.order o INNER JOIN o.customer c WHERE c.id = :userId")
    List<Ticket> findTicketsByUserId(@Param("userId") long userId);

    @Modifying
    @Query("DELETE Ticket t WHERE t.id = :id")
    void cancelReservedTicket(@Param("id") long id);

    @Modifying
    @Query("UPDATE Ticket t SET t.valid = false WHERE t.order.id = :orderId")
    void invalidateAllTicketsForOrder(@Param("orderId") long orderId);

    @Modifying
    @Query("UPDATE Ticket t SET t.valid = true WHERE t.order.id = :orderId")
    void setValidAllTicketsForOrder(@Param("orderId") long orderId);

    @Query("SELECT COUNT(t) > 0 FROM Ticket t WHERE t.show.id = :showId AND t.hallSpot.id = :seatId")
    boolean existsValidTicketForShowAndSeat(@Param("showId") long showId, @Param("seatId") long seatId);

    Ticket findByHash(String hash);

    @Modifying
    @Query("UPDATE Ticket t SET t.reserved = :setReserved WHERE t.id = :ticketId")
    void changeTicketReserved(@Param("ticketId") long ticketId, @Param("setReserved") boolean setReserved);

    @Query("SELECT t FROM Ticket t INNER JOIN t.show s WHERE s.id = :showId")
    List<Ticket> findTicketsByShowId(@Param("showId") long showId);

    @EntityGraph(attributePaths = {"order.customer"})
    Page<Ticket> findAll(Specification<Ticket> specification, Pageable pageable);

    @Modifying
    @Query("UPDATE Ticket t SET t.valid = true WHERE t.id = :id")
    void validateTicketById(@Param("id") long id);
}
