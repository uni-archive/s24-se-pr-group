package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface NewsService {
    NewsDto getNewsById(Long id) throws DtoNotFoundException;

    NewsDto createNews(NewsDto newsDto) throws IOException, ValidationException;

    Page<NewsDto> getAllNews(Pageable pageable);

    Page<NewsDto> getUnseenNews(Pageable pageable) throws DtoNotFoundException;
}
