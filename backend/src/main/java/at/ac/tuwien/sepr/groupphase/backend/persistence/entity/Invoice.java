package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.InvoiceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Invoice extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @Column(name = "dateTime")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateTime;

    @Enumerated
    private InvoiceType invoiceType;

    public Invoice(Order order, LocalDateTime invoiceDate, InvoiceType invoiceType) {
        this.order = order;
        this.dateTime = invoiceDate;
        this.invoiceType = invoiceType;
    }

    public Invoice() {
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime invoiceDate) {
        this.dateTime = invoiceDate;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Invoice invoice = (Invoice) o;
        return Objects.equals(order, invoice.order) && Objects.equals(dateTime, invoice.dateTime) && invoiceType == invoice.invoiceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, dateTime, invoiceType);
    }

    @Override
    public String toString() {
        return "Invoice{" + "order=" + order + ", dateTime=" + dateTime + ", invoiceType=" + invoiceType + '}';
    }
}

