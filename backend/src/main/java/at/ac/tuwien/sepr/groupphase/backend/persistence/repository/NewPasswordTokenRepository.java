package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.NewPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewPasswordTokenRepository extends JpaRepository<NewPasswordToken, Long> {

    NewPasswordToken findByToken(String token);

    List<NewPasswordToken> findByEmail(String email);
}
