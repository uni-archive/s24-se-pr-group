/*package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {


    List<News> findAllByOrderByPublishedAtDesc();

}
*/

package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findAllByOrderByPublishedAtDesc();


    @Query("SELECT n FROM News n " + "WHERE n.id NOT IN (SELECT nu.id FROM News nu JOIN nu.users u WHERE u.id = :userId) " + "ORDER BY n.publishedAt DESC")
    List<News> findUnseenNewsByUser(@Param("userId") Long userId);

}
