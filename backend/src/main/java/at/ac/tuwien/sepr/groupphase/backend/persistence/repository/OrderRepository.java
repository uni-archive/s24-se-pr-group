package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
    @Query("SELECT o, SIZE(o.tickets), SUM(CASE WHEN t.reserved THEN 0 ELSE hss.price END) " +
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
    @Query("SELECT o, SIZE(o.tickets), SUM(CASE WHEN hss.sector.id != t.hallSpot.sector.id or hss.show.id != t.show.id then 0 WHEN t.reserved THEN 0 ELSE hss.price END), SUM(CASE WHEN hss.sector.id != t.hallSpot.sector.id or hss.show.id != t.show.id then 0 WHEN t.reserved THEN hss.price ELSE 0 END) " +
        "FROM Order o " +
        "LEFT JOIN o.tickets t, HallSectorShow hss " +
        "WHERE o.customer.id = :userId " +
        "AND (hss.sector.id = t.hallSpot.sector.id) " +
        "AND (hss.show.id = t.show.id) " +
        "GROUP BY o.id")
    List<Object[]> findForUser(@Param("userId") long userId);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :userId")
    Page<Order> findForUserPaged(@Param("userId") long userId, Pageable pageable);


    @Query("SELECT SIZE(o.tickets) FROM Order o WHERE o.id = :orderId")
    int getTicketCount(@Param("orderId") long orderId);

    @Query("SELECT hss.price FROM Order o INNER JOIN o.tickets t, HallSectorShow hss WHERE o.id = :orderId AND t.hallSpot.sector.id = hss.sector.id AND t.show.id = hss.show.id AND NOT t.reserved")
    List<Long> getTotalNonReservedTicketPrice(@Param("orderId") long orderId);

    @Query("SELECT SUM(hss.price) FROM Order o INNER JOIN o.tickets t, HallSectorShow hss WHERE o.id = :orderId AND t.hallSpot.sector.id = hss.sector.id AND t.show.id = hss.show.id AND t.reserved")
    Optional<Integer> getTotalReservedTicketPrice(@Param("orderId") long orderId);

    @Query("SELECT SUM(hss.price) FROM Order o INNER JOIN o.tickets t, HallSectorShow hss WHERE o.id = :orderId AND t.hallSpot.sector.id = hss.sector.id AND t.show.id = hss.show.id AND t.reserved AND NOT t.valid")
    Optional<Integer> getTotalOpenReservedTicketPrice(@Param("orderId") long orderId);
}
