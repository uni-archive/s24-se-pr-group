package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EmailChangeToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailChangeTokenRepository extends JpaRepository<EmailChangeToken, Long> {

    List<EmailChangeToken> findByCurrentEmail(String currentEmail);
}
