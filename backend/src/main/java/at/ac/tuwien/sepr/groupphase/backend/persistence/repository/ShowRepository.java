package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
}
