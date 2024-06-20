package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HallPlanRepository extends JpaRepository<HallPlan, Long> {

    List<HallPlan> findHallPlansByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT s.location.hallPlan FROM Show s WHERE s.id = :showId")
    HallPlan getHallPlanByShowId(@Param("showId") long showId);
}
