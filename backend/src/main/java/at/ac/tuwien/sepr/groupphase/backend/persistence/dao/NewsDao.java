package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.mapper.NewsMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NewsDao extends AbstractDao<News, NewsDto> {
    private final NewsRepository newsRepository;

    public NewsDao(NewsRepository repository, NewsMapper mapper) {
        super(repository, mapper);
        this.newsRepository = repository;
    }

    public List<NewsDto> findAllByOrderByPublishedAtDesc() {
        return newsRepository.findAllByOrderByPublishedAtDesc().stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }
}

