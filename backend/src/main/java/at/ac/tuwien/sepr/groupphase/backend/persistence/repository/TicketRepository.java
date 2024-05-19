package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t FROM Ticket t INNER JOIN t.order o INNER JOIN o.customer c WHERE c.id = :userId")
    List<Ticket> findTicketsByUserId(@Param("userId") long userId);

    @Modifying
    @Query("DELETE Ticket t WHERE t.id = :id")
    void cancelReservedTicket(@Param("id") long id);
}
