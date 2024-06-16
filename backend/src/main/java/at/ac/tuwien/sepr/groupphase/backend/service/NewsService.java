package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface NewsService {

    /**
     * Retrieves a news entry by its ID.
     *
     * @param id the ID of the news entry to retrieve
     * @return the retrieved {@link NewsDto}
     * @throws DtoNotFoundException if the news entry with the specified ID is not found
     */
    NewsDto getNewsById(Long id) throws DtoNotFoundException;

    /**
     * Creates a new news entry.
     *
     * @param newsDto the DTO containing the details of the news entry to create
     * @throws IOException if an I/O error occurs during the creation of the news entry
     * @throws ValidationException if the news entry fails validation
     */
    void createNews(NewsDto newsDto) throws IOException, ValidationException;

    /**
     * Retrieves a paginated list of all news entries.
     *
     * @param pageable the pagination information
     * @return a paginated list of all news entries as {@link Page<NewsDto>}
     */
    Page<NewsDto> getAllNews(Pageable pageable);

    /**
     * Retrieves a paginated list of unseen news entries.
     *
     * @param pageable the pagination information
     * @return a paginated list of unseen news entries as {@link Page<NewsDto>}
     * @throws DtoNotFoundException if the unseen news entries are not found
     */
    Page<NewsDto> getUnseenNews(Pageable pageable) throws DtoNotFoundException;
}

