package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Autowired
    public NewsEndpoint(NewsService newsService) {
        this.newsService = newsService;
    }

    @Secured("ROLE_USER")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get list of news without details", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<List<NewsDto>> findAll() {
        LOGGER.info("GET /api/v1/news");
        List<NewsDto> newsList = newsService.getAllNews();
        return ResponseEntity.ok(newsList);
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get detailed information about a specific news", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<NewsDto> find(@PathVariable(name = "id") Long id) {
        LOGGER.info("GET /api/v1/news/{}", id);
        try {
            NewsDto newsDto = newsService.getNewsById(id);
            return ResponseEntity.ok(newsDto);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Publish a new news", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<NewsDto> create(@RequestParam("image") MultipartFile file, @RequestParam("title") String title, @RequestParam("summary") String summary, @RequestParam("text") String text) {
        NewsDto newsDto = new NewsDto();
        newsDto.setTitle(title);
        newsDto.setSummary(summary);
        newsDto.setText(text);

        try {
            newsDto.setImage(file.getBytes());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }

        LOGGER.info("POST /api/v1/news body: {}", newsDto);

        try {
            newsService.createNews(newsDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(newsDto);
        } catch (IOException | ValidationException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
    }
}
