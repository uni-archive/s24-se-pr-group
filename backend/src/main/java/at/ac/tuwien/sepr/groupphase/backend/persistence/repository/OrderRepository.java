package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Gets an order with detailed information about each ticket that is included.
     *
     * @param id The order-id
     * @return {@link Order}
     */
    Optional<Order> findById(long id);

    /**
     * Gets an order with summarized information.
     *
     * @param id The order-id
     */
    @Query("SELECT o, SIZE(o.tickets), SUM(hss.price) " +
        "FROM Order o " +
        "JOIN o.tickets t " +
        "JOIN t.hallSpot hs " +
        "JOIN hs.sector sec, HallSectorShow hss " +
        "WHERE o.id = :id " +
        "AND hss.sector.id = sec.id " +
        "GROUP BY o")
    Optional<Object[]> findSummaryById(@Param("id") long id);

    /**
     * Finds all orders associated with a given user.
     *
     * @param userId the id of the user.
     * @return a list of {@link Order} associated with the user.
     */
    @Query("SELECT o, SIZE(o.tickets), SUM(hss.price) " +
        "FROM Order o " +
        "LEFT JOIN o.tickets t " +
        "LEFT JOIN t.hallSpot hs " +
        "LEFT JOIN hs.sector sec, HallSectorShow hss " +
        "WHERE o.customer.id = :userId " +
        "AND (sec IS NULL OR hss.sector.id = sec.id)" +
        "GROUP BY o")
    List<Object[]> findForUser(@Param("userId") long userId);
}
