package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.AccountActivateToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountActivateTokenRepository extends JpaRepository<AccountActivateToken, Long> {

    AccountActivateToken findByToken(String token);

    List<AccountActivateToken> findByEmail(String email);
}
