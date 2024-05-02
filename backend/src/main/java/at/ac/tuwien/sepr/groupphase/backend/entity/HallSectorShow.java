package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class HallSectorShow extends AEntity {

    @ManyToOne
    @JoinColumn(name = "SHOW_ID")
    private Show show;

    @ManyToOne
    @JoinColumn(name = "SECTOR_ID")
    private HallSector sector;

    @Column(name = "PRICE")
    private long price;
}
