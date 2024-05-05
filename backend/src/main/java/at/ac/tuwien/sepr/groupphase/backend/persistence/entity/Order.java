package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ORDERS")
public class Order extends AbstractEntity {
    @OneToMany(mappedBy = "order")
    private List<Ticket> tickets;

    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID")
    private ApplicationUser customer;

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

    public Order() {
    }

    public Order(List<Ticket> tickets, ApplicationUser customer) {
        this.tickets = tickets;
        this.customer = customer;
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
