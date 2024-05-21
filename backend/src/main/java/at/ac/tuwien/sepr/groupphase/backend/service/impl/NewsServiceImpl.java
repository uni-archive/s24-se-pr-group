package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.NewsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NewsServiceImpl implements NewsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsRepository newsRepository;

    private final NewsValidator newsValidator;

    public NewsServiceImpl(NewsRepository newsRepository, NewsValidator newsValidator) {
        this.newsRepository = newsRepository;
      this.newsValidator = newsValidator;
    }

    @Override
    public List<News> findAll() {
        LOGGER.debug("Find all news");
        return newsRepository.findAllByOrderByPublishedAtDesc();
    }

    @Override
    public News findOne(Long id) {
        LOGGER.debug("Find news with id {}", id);
        Optional<News> news = newsRepository.findById(id);
        if (news.isPresent()) {
            return news.get();
        } else {
            throw new NotFoundException(String.format("Could not find news with id %s", id));
        }
    }

    @Override
    public News publishNews(News news) throws ValidationException {
        LOGGER.debug("Publish new news {}", news);
        news.setPublishedAt(LocalDateTime.now());
        news.setTitle(news.getTitle().trim());
        news.setSummary(news.getSummary().trim());
        news.setText(news.getText().trim());

        newsValidator.validateForPublish(news);
        return newsRepository.save(news);
    }


}
