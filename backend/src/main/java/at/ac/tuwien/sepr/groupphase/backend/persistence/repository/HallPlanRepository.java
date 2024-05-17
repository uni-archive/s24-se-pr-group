package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HallPlanRepository extends JpaRepository<HallPlan, Long> {
}
