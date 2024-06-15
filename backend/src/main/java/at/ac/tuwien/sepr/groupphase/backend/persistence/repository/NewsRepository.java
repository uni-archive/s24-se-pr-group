package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    Page<News> findAllByOrderByPublishedAtDesc(Pageable pageable);

    @Query("SELECT n FROM News n LEFT JOIN n.users u ON u.id = :userId WHERE u.id IS NULL ORDER BY n.publishedAt DESC")
    Page<News> findUnseenNewsByUser(@Param("userId") Long userId, Pageable pageable);
}
