package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.NewsValidator;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class NewsServiceImplTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private NewsValidator newsValidator;

    @InjectMocks
    private NewsServiceImpl newsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void validateForFindAllShouldReturnAllNewsOrderedByPublishedAtDesc() {

        News news1 = new News();
        news1.setPublishedAt(LocalDateTime.now().minusDays(1));
        News news2 = new News();
        news2.setPublishedAt(LocalDateTime.now());

        when(newsRepository.findAllByOrderByPublishedAtDesc()).thenReturn(Arrays.asList(news2, news1));

        List<News> newsList = newsService.findAll();

        assertEquals(2, newsList.size());
        assertEquals(news2, newsList.get(0));
        assertEquals(news1, newsList.get(1));
    }

    @Test
    public void validateForFindOneShouldReturnNews() {

        Long id = -1L;
        News news = new News();
        news.setId(id);

        when(newsRepository.findById(id)).thenReturn(Optional.of(news));

        News foundNews = newsService.findOne(id);

        assertEquals(news, foundNews);
    }

    @Test
    public void validateForFindOneShouldThrowNotFoundExceptionIfNewsDoesNotExist() {

        Long id = -1L;

        when(newsRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> newsService.findOne(id));
    }

    @Test
    public void validateForPublishNewsShouldThrowValidationExceptionForInvalidNews() throws ValidationException {

        News news = new News();

        doThrow(new ValidationException("Validation failed")).when(newsValidator).validateForPublish(news);

        assertThrows(ValidationException.class, () -> newsService.publishNews(news));
    }

}
