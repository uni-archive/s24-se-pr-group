package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsResponseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.NewsEndpointMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/news")
public class NewsEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsService newsService;
    private final NewsEndpointMapper newsEndpointMapper;

    @Autowired
    public NewsEndpoint(NewsService newsService, NewsEndpointMapper newsEndpointMapper) {
        this.newsService = newsService;
        this.newsEndpointMapper = newsEndpointMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get list of news without details")
    public ResponseEntity<List<NewsResponseDto>> findAll() throws EntityNotFoundException {
        LOGGER.info("GET /api/v1/news");
        List<NewsDto> newsList = newsService.getAllNews();
        return ResponseEntity.ok(newsEndpointMapper.toResponseList(newsList));
    }
    @Secured("ROLE_USER")
    @GetMapping(value = "/unread", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get list of unread news")
    public ResponseEntity<List<NewsResponseDto>> findUnread() throws EntityNotFoundException {
        LOGGER.info("GET /api/v1/news/unread");
        List<NewsDto> unreadNewsList = newsService.getUnseenNews();
        return ResponseEntity.ok(newsEndpointMapper.toResponseList(unreadNewsList));
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get detailed information about a specific news")
    public ResponseEntity<NewsResponseDto> find(@PathVariable(name = "id") Long id) {
        LOGGER.info("GET /api/v1/news/{}", id);
        try {
            NewsDto newsDto = newsService.getNewsById(id);
            return ResponseEntity.ok(newsEndpointMapper.toResponse(newsDto));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Publish a new news")
    public ResponseEntity<NewsResponseDto> create(@RequestParam("image") MultipartFile file,
                                                  @RequestParam("title") String title,
                                                  @RequestParam("summary") String summary,
                                                  @RequestParam("text") String text) {
        NewsRequestDto newsRequestDto = new NewsRequestDto();
        newsRequestDto.setTitle(title);
        newsRequestDto.setSummary(summary);
        newsRequestDto.setText(text);

        try {
            newsRequestDto.setImage(file.getBytes());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }

        LOGGER.info("POST /api/v1/news body: {}", newsRequestDto);

        try {
            NewsDto createdNews = newsService.createNews(newsEndpointMapper.toDto(newsRequestDto));
            return ResponseEntity.status(HttpStatus.CREATED).body(newsEndpointMapper.toResponse(createdNews));
        } catch (IOException | ValidationException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
    }
}
