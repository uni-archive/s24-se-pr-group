package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSeat;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HallSectorRepository extends JpaRepository<HallSector, Long> {

    List<HallSector> findByHallPlanId(Long hallPlanId);

    @Query("SELECT hs FROM HallSector hs JOIN hs.seats s WHERE s.id = :spotId")
    HallSector findBySeatId(@Param("spotId") Long spotId);

    @Query("SELECT hs FROM HallSector hs WHERE hs.hallPlan.id IN :hallPlanIds")
    List<HallSector> findSectorsByHallPlanIds(@Param("hallPlanIds") List<Long> hallPlanIds);
}
