package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {

    ApplicationUser findByEmail(String email);

    @Modifying
    @Query("UPDATE ApplicationUser u SET u.accountLocked = :status WHERE u.email = :email")
    @Transactional
    void updateStatusByEmail(@Param("status") boolean status, @Param("email") String email);

}