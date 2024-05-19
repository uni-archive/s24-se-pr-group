package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    /**
     * Finds all (up to 2) invoices related to an order.
     *
     * @param id the order-id
     * @return List of {@link Invoice}
     */
    List<Invoice> findByOrderId(long id);
}
