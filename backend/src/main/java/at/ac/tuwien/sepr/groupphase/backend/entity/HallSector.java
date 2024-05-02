package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class HallSector extends AEntity {
    @ManyToOne
    @JoinColumn(name="HALL_PLAN_ID")
    private HallPlan hallPlan;

    @Column(name = "NAME")
    private String name;

    @Column(name = "FRONTEND_COORDINATES")
    private String frontendCoordinates;

    @OneToMany(mappedBy = "sector")
    private List<HallSeat> seats;

}
