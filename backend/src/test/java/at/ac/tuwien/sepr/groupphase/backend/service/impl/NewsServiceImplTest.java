package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.NewsDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.UserDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.NewsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    @Mock
    private NewsDao newsDao;

    @Mock
    private UserDao userDao;

    @Mock
    private NewsValidator newsValidator;

    @InjectMocks
    private NewsServiceImpl newsService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getNewsById_ShouldReturnNews_WhenNewsExists() throws DtoNotFoundException, EntityNotFoundException {
        Long newsId = 1L;
        String username = "user@example.com";
        ApplicationUserDto userDto = new ApplicationUserDto();
        userDto.setEmail(username);
        NewsDto newsDto = new NewsDto();
        newsDto.setId(newsId);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userDao.findByEmail(username)).thenReturn(userDto);
        when(newsDao.findById(newsId)).thenReturn(newsDto);

        NewsDto result = newsService.getNewsById(newsId);

        assertNotNull(result);
        assertEquals(newsId, result.getId());
    }

    @Test
    void getNewsById_ShouldThrowDtoNotFoundException_WhenNewsDoesNotExist() throws EntityNotFoundException {
        Long newsId = 1L;
        when(newsDao.findById(newsId)).thenThrow(new EntityNotFoundException(newsId));

        assertThrows(DtoNotFoundException.class, () -> newsService.getNewsById(newsId));
    }

    @Test
    void createNews_ShouldCreateNews_WhenValid() throws ValidationException {
        NewsDto newsDto = new NewsDto();
        newsDto.setTitle("Valid Title");
        newsDto.setSummary("Valid Summary");
        newsDto.setText("Valid Text");

        doNothing().when(newsValidator).validateForPublish(newsDto);

        newsService.createNews(newsDto);

        verify(newsValidator).validateForPublish(newsDto);
        verify(newsDao).create(newsDto);
    }

    @Test
    void createNews_ShouldThrowValidationException_WhenInvalid() throws ValidationException {
        NewsDto newsDto = new NewsDto();
        doThrow(new ValidationException("Invalid news")).when(newsValidator).validateForPublish(newsDto);

        assertThrows(ValidationException.class, () -> newsService.createNews(newsDto));
    }

    @Test
    void getAllNews_ShouldReturnAllNews() {
        Pageable pageable = PageRequest.of(0, 10);
        NewsDto newsDto = new NewsDto();
        Page<NewsDto> newsPage = new PageImpl<>(List.of(newsDto));

        when(newsDao.findAllByOrderByPublishedAtDesc(pageable)).thenReturn(newsPage);

        Page<NewsDto> result = newsService.getAllNews(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(newsDto, result.getContent().getFirst());
    }

    @Test
    void getUnseenNews_ShouldReturnUnseenNewsForUser() throws DtoNotFoundException, EntityNotFoundException {
        Pageable pageable = PageRequest.of(0, 10);
        String username = "user@example.com";
        ApplicationUserDto userDto = new ApplicationUserDto();
        userDto.setId(1L);
        userDto.setEmail(username);
        NewsDto newsDto = new NewsDto();
        Page<NewsDto> newsPage = new PageImpl<>(List.of(newsDto));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userDao.findByEmail(username)).thenReturn(userDto);
        when(newsDao.findUnseenNewsByUser(userDto.getId(), pageable)).thenReturn(newsPage);

        Page<NewsDto> result = newsService.getUnseenNews(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(newsDto, result.getContent().getFirst());
    }

    @Test
    void getUnseenNews_ShouldThrowDtoNotFoundException_WhenUserNotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        String username = "user@example.com";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userDao.findByEmail(username)).thenThrow(new RuntimeException("User not found"));

        assertThrows(DtoNotFoundException.class, () -> newsService.getUnseenNews(pageable));
    }
}
