package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {

    ApplicationUser findByEmail(String email);

    @Query("SELECT u FROM ApplicationUser u WHERE "
        + "(:firstName IS NULL OR UPPER(u.firstName) LIKE UPPER(CONCAT('%', :firstName, '%'))) AND "
        + "(:familyName IS NULL OR UPPER(u.familyName) LIKE UPPER(CONCAT('%', :familyName, '%'))) AND "
        + "(:email IS NULL OR UPPER(u.email) LIKE UPPER(CONCAT('%', :email, '%'))) AND "
        + "(:isLocked IS NULL OR u.accountLocked = :isLocked)")
    List<ApplicationUser> findByParams(@Param("firstName") String firstName,
                                       @Param("familyName") String familyName,
                                       @Param("email") String email,
                                       @Param("isLocked") boolean isLocked);

}