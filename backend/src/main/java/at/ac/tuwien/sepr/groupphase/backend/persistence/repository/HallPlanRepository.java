package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HallPlanRepository extends JpaRepository<HallPlan, Long> {

    @Query("SELECT h FROM HallPlan h WHERE h.name = :name")
    List<HallPlan> findByName(@Param("name") String name);

    @Query("SELECT s.location.hallPlan FROM Show s WHERE s.id = :showId")
    HallPlan getHallPlanByShowId(@Param("showId") long showId);
}
