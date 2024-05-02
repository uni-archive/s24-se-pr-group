package at.ac.tuwien.sepr.groupphase.backend.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

@Entity
public class Ticket extends AEntity {

    @Column(name="HASH")
    private String hash;
    @Column(name="RESERVED")
    private boolean reserved;
    @Column(name="VALID")
    private boolean valid;

    @ManyToOne
    @JoinColumn(name = "HALL_SPOT_ID")
    private HallSpot hallSpot;

    @ManyToOne
    @JoinColumn(name = "SHOW_ID")
    private Show show;

    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    public Ticket() {
    }

    public Ticket(String hash, boolean reserved, boolean valid, HallSpot hallSpot, Show show, Order order) {
        this.hash = hash;
        this.reserved = reserved;
        this.valid = valid;
        this.hallSpot = hallSpot;
        this.show = show;
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return reserved == ticket.reserved && valid == ticket.valid && Objects.equals(hash, ticket.hash) && Objects.equals(hallSpot, ticket.hallSpot) && Objects.equals(show, ticket.show) && Objects.equals(order, ticket.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash, reserved, valid, hallSpot, show, order);
    }

    @Override
    public String toString() {
        return "Ticket{" +
            "hash='" + hash + '\'' +
            ", reserved=" + reserved +
            ", valid=" + valid +
            ", hallSpot=" + hallSpot +
            ", show=" + show +
            ", order=" + order +
            '}';
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public HallSpot getHallSpot() {
        return hallSpot;
    }

    public void setHallSpot(HallSpot hallSpot) {
        this.hallSpot = hallSpot;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
