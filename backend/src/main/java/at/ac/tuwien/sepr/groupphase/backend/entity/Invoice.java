package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

@Entity
public class Invoice extends AEntity{

    @ManyToOne
    @JoinColumn(name="ORDER_ID")
    private Order order;

    public Invoice(Order order) {
        this.order = order;
    }

    public Invoice() {
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return Objects.equals(order, invoice.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order);
    }

    @Override
    public String toString() {
        return "Invoice{" +
            "order=" + order +
            '}';
    }
}
