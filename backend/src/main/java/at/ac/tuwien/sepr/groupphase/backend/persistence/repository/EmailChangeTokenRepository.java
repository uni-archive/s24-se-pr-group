package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EmailChangeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailChangeTokenRepository extends JpaRepository<EmailChangeToken, Long> {

    EmailChangeToken findByToken(String token);

    List<EmailChangeToken> findByCurrentEmail(String currentEmail);
}
