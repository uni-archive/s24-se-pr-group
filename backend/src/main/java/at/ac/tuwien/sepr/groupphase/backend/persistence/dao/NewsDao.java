package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.mapper.NewsMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NewsDao extends AbstractDao<News, NewsDto> {
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    public NewsDao(NewsRepository repository, NewsMapper mapper, UserRepository userRepository) {
        super(repository, mapper);
        this.newsRepository = repository;
        this.userRepository = userRepository;
    }

    public List<NewsDto> findAllByOrderByPublishedAtDesc() {
        return newsRepository.findAllByOrderByPublishedAtDesc().stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    public void updateNewsWithUsers(NewsDto newsDto) throws EntityNotFoundException {
        if (!repository.existsById(newsDto.getId())) {
            throw new EntityNotFoundException(newsDto.getId());
        }
        News news = mapper.toEntity(newsDto);
        for (ApplicationUserDto userDto : newsDto.getUsers()) {
            ApplicationUser user = userRepository.findByEmail(userDto.getEmail());
            if (!user.getNews().contains(news)) {
                user.getNews().add(news);
                userRepository.save(user);
            }
        }
        repository.save(news);
    }

    public List<NewsDto> findUnseenNewsByUser(Long userId) throws EntityNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(userId);
        }
        List<News> unseenNews = newsRepository.findUnseenNewsByUser(userId);
        return unseenNews.stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

}

