package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ORDERS")
public class Order extends AbstractEntity {

    // Viewing an order requires infos of tickets which is spread across the domain-model
    // Therefore fetching everything about tickets here eagerly is most likely much better than doing it lazily.
    @OneToMany(mappedBy = "order")
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "order")
    private List<Invoice> invoices;

    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID")
    private ApplicationUser customer;

    @Column(name = "dateTime")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateTime;


    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public ApplicationUser getCustomer() {
        return customer;
    }

    public void setCustomer(ApplicationUser customer) {
        this.customer = customer;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Order() {
    }

    public Order(List<Ticket> tickets, ApplicationUser customer, List<Invoice> invoices) {
        this.tickets = tickets;
        this.customer = customer;
        this.invoices = invoices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(tickets, order.tickets) && Objects.equals(customer, order.customer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tickets, customer);
    }

    @Override
    public String toString() {
        return "Order{" + "tickets=" + tickets + ", customer=" + customer + '}';
    }
}
