package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.NewsDao;
import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.UserDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.NewsValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NewsServiceImpl implements NewsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    private final NewsDao newsDao;
    private final UserDao userDao;

    private final NewsValidator newsValidator;

    @Autowired
    public NewsServiceImpl(NewsDao newsDao, UserDao userDao, NewsValidator newsValidator) {

        this.newsDao = newsDao;
        this.userDao = userDao;
        this.newsValidator = newsValidator;
    }

    @Override
    @Transactional
    public NewsDto getNewsById(Long id) throws EntityNotFoundException {
        LOGGER.debug("Find news with id {}", id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ApplicationUserDto userDto = userDao.findByEmail(username);

        NewsDto newsDto = newsDao.findById(id);


        if (newsDto.getUsers() == null) {
            newsDto.setUsers(new ArrayList<>());
        }


        if (!newsDto.getUsers().contains(userDto)) {
            newsDto.getUsers().add(userDto);
            newsDao.updateNewsWithUsers(newsDto);
        }

        return newsDto;
    }


    @Override
    public NewsDto createNews(NewsDto newsDto) throws ValidationException {
        LOGGER.debug("Create news {}", newsDto);
        newsDto.setPublishedAt(LocalDateTime.now());
        if (newsDto.getTitle() != null) {
            newsDto.setTitle(newsDto.getTitle().trim());
        }
        if (newsDto.getSummary() != null) {
            newsDto.setSummary(newsDto.getSummary().trim());
        }
        if (newsDto.getText() != null) {
            newsDto.setText(newsDto.getText().trim());
        }
        newsValidator.validateForPublish(newsDto);
        return newsDao.create(newsDto);
    }

    @Override
    public List<NewsDto> getAllNews() {
        LOGGER.debug("Find all news");
        return newsDao.findAllByOrderByPublishedAtDesc();
    }

    @Override
    @Transactional
    public List<NewsDto> getUnseenNews() throws EntityNotFoundException {
        LOGGER.debug("Find unseen news for current user");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ApplicationUserDto userDto = userDao.findByEmail(username);

        return newsDao.findUnseenNewsByUser(userDto.getId());
    }
}