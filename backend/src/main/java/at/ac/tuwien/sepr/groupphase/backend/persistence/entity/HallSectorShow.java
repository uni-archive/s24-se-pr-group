package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

@Entity
public class HallSectorShow extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "SHOW_ID")
    private Show show;

    @ManyToOne
    @JoinColumn(name = "SECTOR_ID")
    private HallSector sector;

    @Column(name = "PRICE")
    private long price;

    public HallSectorShow() {
    }

    public HallSectorShow(Show show, HallSector sector, long price) {
        this.show = show;
        this.sector = sector;
        this.price = price;
    }

    @Override
    public String toString() {
        return "HallSectorShow{" + "show=" + show + ", sector=" + sector + ", price=" + price + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HallSectorShow that = (HallSectorShow) o;
        return price == that.price && Objects.equals(show, that.show) && Objects.equals(sector, that.sector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(show, sector, price);
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public HallSector getSector() {
        return sector;
    }

    public void setSector(HallSector sector) {
        this.sector = sector;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
