package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;

import jakarta.validation.Valid;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;

@RestController
@RequestMapping(value = "/api/v1/news")
public class NewsEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsService newsService;
    private final NewsMapper newsMapper;

    @Autowired
    public NewsEndpoint(NewsService newsService, NewsMapper newsMapper) {
        this.newsService = newsService;
        this.newsMapper = newsMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get list of news without details", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleNewsDto> findAll() {
        LOGGER.info("GET /api/v1/news");
        return newsMapper.newsToSimpleNewsDto(newsService.findAll());
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get detailed information about a specific news", security = @SecurityRequirement(name = "apiKey"))
    public DetailedNewsDto find(@PathVariable(name = "id") Long id) {
        LOGGER.info("GET /api/v1/news/{}", id);
        return newsMapper.newsToDetailedNewsDto(newsService.findOne(id));
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Publish a new news", security = @SecurityRequirement(name = "apiKey"))
    public DetailedNewsDto create(@Valid @RequestParam("image") MultipartFile file,
                                  @Valid @RequestParam("title") String title,
                                  @Valid @RequestParam("summary") String summary,
                                  @Valid @RequestParam("text") String text) throws IOException, SQLException {

        NewsInquiryDto newsInquiryDto = new NewsInquiryDto();
        newsInquiryDto.setTitle(title);
        newsInquiryDto.setSummary(summary);
        newsInquiryDto.setText(text);

        Blob imageBlob = new SerialBlob(file.getBytes());

        newsInquiryDto.setImage(imageBlob);


        LOGGER.info("POST /api/v1/news body: {}", newsInquiryDto);

        return newsMapper.newsToDetailedNewsDto(
            newsService.publishNews(newsMapper.newsInquiryDtoToNews(newsInquiryDto)));
    }





}




