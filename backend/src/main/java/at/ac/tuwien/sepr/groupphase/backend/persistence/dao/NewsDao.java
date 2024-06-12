package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.CycleAvoidingMappingContext;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.NewsMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class NewsDao extends AbstractDao<News, NewsDto> {
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final NewsMapper mapper;


    @Override
    public NewsDto findById(Long id) throws EntityNotFoundException {
        return mapper.toDtoWithContext(newsRepository.findById(id).orElseThrow(()
            -> new EntityNotFoundException(id)), new CycleAvoidingMappingContext());
    }

    public NewsDao(NewsRepository repository, NewsMapper mapper, UserRepository userRepository) {
        super(repository, mapper);
        this.newsRepository = repository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public Page<NewsDto> findAllByOrderByPublishedAtDesc(Pageable pageable) {
        return newsRepository.findAllByOrderByPublishedAtDesc(pageable)
            .map(news -> mapper.toDtoWithContext(news, new CycleAvoidingMappingContext()));
    }

    public void updateNewsWithUsers(NewsDto newsDto) throws EntityNotFoundException {
        if (!repository.existsById(newsDto.getId())) {
            throw new EntityNotFoundException(newsDto.getId());
        }
        News news = mapper.toEntityWithContext(newsDto, new CycleAvoidingMappingContext());
        for (ApplicationUserDto userDto : newsDto.getUsers()) {
            ApplicationUser user = userRepository.findByEmail(userDto.getEmail());
            if (!user.getNews().contains(news)) {
                user.getNews().add(news);
                userRepository.save(user);
            }
        }
        repository.save(news);
    }

    public Page<NewsDto> findUnseenNewsByUser(Long userId, Pageable pageable) throws EntityNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(userId);
        }
        return newsRepository.findUnseenNewsByUser(userId, pageable)
            .map(news -> mapper.toDtoWithContext(news, new CycleAvoidingMappingContext()));
    }
}
