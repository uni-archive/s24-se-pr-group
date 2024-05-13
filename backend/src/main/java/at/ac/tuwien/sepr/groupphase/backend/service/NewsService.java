package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import java.util.List;

public interface NewsService {

    /**
     * Find all news entries ordered by published at date (descending).
     *
     * @return ordered list of all news entries
     */
    List<News> findAll();


    /**
     * Find a single news entry by id.
     *
     * @param id the id of the news entry
     * @return the news entry
     */
    News findOne(Long id);

    /**
     * Publish a single news entry.
     *
     * @param news to publish
     * @return published news entry
     */
    News publishNews(News news);
}
