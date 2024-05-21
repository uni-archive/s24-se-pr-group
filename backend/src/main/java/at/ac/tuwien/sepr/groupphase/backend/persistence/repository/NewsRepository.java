package at.ac.tuwien.sepr.groupphase.backend.persistence.repository;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    /**
     * Find all news entries ordered by published at date (descending).
     *
     * @return ordered list of all news entries
     */
    List<News> findAllByOrderByPublishedAtDesc();

}
