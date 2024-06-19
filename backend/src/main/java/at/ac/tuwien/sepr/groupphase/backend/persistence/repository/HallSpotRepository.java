package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HallSpotRepository extends JpaRepository<HallSpot, Long> {

    List<HallSpot> findBySectorId(Long hallSectorId);

    @Query("SELECT hsp FROM HallSpot hsp WHERE hsp.sector.id IN :hallSectorIds")
    List<HallSpot> findSpotsByHallSectorIds(@Param("hallSectorIds") List<Long> hallSectorIds);
}
