package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsResponseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleNewsResponseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.NewsEndpointMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

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

    @PermitAll
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get list of news without details")
    public ResponseEntity<Page<SimpleNewsResponseDto>> findAll(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                               @RequestParam(name = "size", defaultValue = "9") Integer size) {
        LOGGER.info("GET /api/v1/news");
        PageRequest pageable = PageRequest.of(page, size);
        Page<NewsDto> newsList = newsService.getAllNews(pageable);
        return ResponseEntity.ok(newsList.map(newsEndpointMapper::toSimpleResponse));
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/unread", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get list of unread news without details")
    public ResponseEntity<Page<SimpleNewsResponseDto>> findUnread(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                  @RequestParam(name = "size", defaultValue = "9") Integer size) {
        LOGGER.info("GET /api/v1/news/unread");
        PageRequest pageable = PageRequest.of(page, size);
        Page<NewsDto> unreadNewsList;
        try {
            unreadNewsList = newsService.getUnseenNews(pageable);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
        return ResponseEntity.ok(unreadNewsList.map(newsEndpointMapper::toSimpleResponse));
    }

    @PermitAll
    @GetMapping(value = "/detail/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get detailed information about a specific news")
    public ResponseEntity<NewsResponseDto> find(@PathVariable("id") Long id) {
        LOGGER.info("GET /api/v1/news/detail/{}", id);
        try {
            NewsDto newsDto = newsService.getNewsById(id);
            return ResponseEntity.ok(newsEndpointMapper.toResponse(newsDto));
        } catch (DtoNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Publish a new news")
    public ResponseEntity<NewsResponseDto> create(@RequestPart("image") MultipartFile file,
                                                  @RequestPart("news") NewsRequestDto newsRequestDto) {

        try {
            newsRequestDto.setImage(file.getBytes());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image data", e);
        }

        LOGGER.info("POST /api/v1/news body: {}", newsRequestDto);

        try {
            newsService.createNews(newsEndpointMapper.toDto(newsRequestDto));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IOException | ValidationException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
    }
}