package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.NewsEndpointMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class NewsEndpointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NewsService newsService;

    @Autowired
    private NewsEndpointMapper newsEndpointMapper;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        newsRepository.deleteAll();
        userRepository.deleteAll();
    }

    private byte[] createValidTestImage() throws IOException {
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void findAllShouldReturnNewsList() throws Exception {
        NewsDto newsDto = new NewsDto();
        newsDto.setTitle("Test Title");
        newsDto.setSummary("Test Summary");
        newsDto.setText("Test Text");
        newsDto.setImage(createValidTestImage());
        newsService.createNews(newsDto);

        mockMvc.perform(get("/api/v1/news/all")
                .param("page", "0")
                .param("size", "9")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].title").value("Test Title"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void createNewsShouldReturnForbiddenForNonAdmin() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, createValidTestImage());
        String title = "Test Title";
        String summary = "Test Summary";
        String text = "Test Text";
        EventDto eventDto = new EventDto();
        eventDto.setTitle("Test Event");

        NewsRequestDto newsRequestDto = new NewsRequestDto();
        newsRequestDto.setTitle(title);
        newsRequestDto.setSummary(summary);
        newsRequestDto.setText(text);
        newsRequestDto.setEventDto(eventDto);

        String newsRequestString = new ObjectMapper().writeValueAsString(newsRequestDto);
        MockPart newsRequestPart = new MockPart("news", newsRequestString.getBytes(StandardCharsets.UTF_8));
        newsRequestPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(multipart("/api/v1/news/create")
                .file("image", file.getBytes())
                .part(newsRequestPart))
            .andExpect(status().isForbidden());
    }

    /*
    @Test
    @WithMockUser(roles = "USER", username = "admin2@email.com")
    void findUnreadShouldReturnUnreadNewsList() throws Exception {
        NewsDto newsDto = new NewsDto();
        newsDto.setTitle("Test Title");
        newsDto.setSummary("Test Summary");
        newsDto.setText("Test Text");
        newsDto.setImage(createValidTestImage());
        newsService.createNews(newsDto);

        mockMvc.perform(get("/api/v1/news/unread")
                .param("page", "0")
                .param("size", "9")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].title").value("Test Title"));
    }

    @Test
    @WithMockUser(username = "user")
    void findByIdShouldReturnNewsDetails() throws Exception {
        NewsDto newsDto = new NewsDto();
        newsDto.setTitle("Test Title");
        newsDto.setSummary("Test Summary");
        newsDto.setText("Test Text");
        newsDto.setImage(createValidTestImage());
        NewsDto savedNews = newsService.createNews(newsDto);

        mockMvc.perform(get("/api/v1/news/{id}", savedNews.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createNewsShouldReturnCreatedStatus() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, createValidTestImage());
        String title = "Test Title";
        String summary = "Test Summary";
        String text = "Test Text";
        EventDto eventDto = new EventDto();
        eventDto.setTitle("Test Event");

        mockMvc.perform(multipart("/api/v1/news")
                .file(file)
                .param("title", title)
                .param("summary", summary)
                .param("text", text)
                .param("event", new ObjectMapper().writeValueAsString(eventDto)))
            .andExpect(status().isCreated());
    }


   */

}
