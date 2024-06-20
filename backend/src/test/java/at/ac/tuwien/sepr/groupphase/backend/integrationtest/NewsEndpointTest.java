package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsResponseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleNewsResponseDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class NewsEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        newsRepository.deleteAll();
    }

    private byte[] createValidTestImage() throws IOException {
        BufferedImage image = new BufferedImage(1200, 600, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }

    void setUpWith9News() throws IOException {
        for (int i = 1; i <= 9; i++) {
            News news = new News();
            news.setId((long) -i);
            news.setTitle("Title");
            news.setSummary("Summary");
            news.setText("Text");
            news.setPublishedAt(LocalDateTime.now());
            news.setImage(createValidTestImage());
            newsRepository.save(news);
        }
    }

    @Test
    void findAllShouldReturn9NewsWithData() throws Exception {
        setUpWith9News();

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/news/all").contentType(MediaType.APPLICATION_JSON)
            .param("page", "0").param("size", "9")).andExpect(status().isOk()).andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        ObjectNode node = objectMapper.readValue(content, ObjectNode.class);

        List<SimpleNewsResponseDto> newsList = objectMapper.convertValue(node.get("content"), new TypeReference<>() {
        });

        Assertions.assertEquals(9, newsList.size());

        for (SimpleNewsResponseDto news : newsList) {
            Assertions.assertEquals(news.getTitle(), "Title");
            Assertions.assertEquals(news.getSummary(), "Summary");
            Assertions.assertNotNull(news.getPublishedAt(), "Published date should not be null");
            Assertions.assertNotNull(news.getImage(), "Image should not be null");
        }
    }

    @Test
    void findUnreadShouldReturnUnreadNews() throws Exception {
        setUpWith9News();

        ApplicationUser user = new ApplicationUser();
        user.setEmail("test@email.com");
        user.setFirstName("John");
        user.setFamilyName("Doe");
        userRepository.save(user);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@email.com");
        when(authentication.getAuthorities()).thenAnswer(invocation -> Collections.singletonList
            (new SimpleGrantedAuthority("ROLE_USER")));
        when(authentication.isAuthenticated()).thenReturn(true);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Page<News> page = newsRepository.findAll(PageRequest.of(0, 3));
        List<News> readNews = page.getContent();
        user.setNews(readNews);
        userRepository.save(user);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/news/unread")
            .contentType(MediaType.APPLICATION_JSON).param("page", "0").param("size", "9"))
            .andExpect(status().isOk()).andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        ObjectNode node = objectMapper.readValue(content, ObjectNode.class);

        List<SimpleNewsResponseDto> newsList = objectMapper.convertValue(node.get("content"), new TypeReference<>() {
        });

        Assertions.assertFalse(newsList.isEmpty(), "There should be unread news");
        for (SimpleNewsResponseDto news : newsList) {
            Assertions.assertFalse(readNews.stream().anyMatch(rn -> rn.getId()
                .equals(news.getId())), "Read news should not be in the unread list");
        }
    }

    @Test
    void shouldReturnNewsDetail() throws Exception {
        setUpWith9News();

        News news = newsRepository.findAll().getFirst();
        Long newsId = news.getId();

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/news/detail/" + newsId)
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        NewsResponseDto newsResponse = objectMapper.readValue(content, NewsResponseDto.class);

        Assertions.assertEquals(news.getId(), newsResponse.getId());
        Assertions.assertEquals(news.getTitle(), newsResponse.getTitle());
        Assertions.assertEquals(news.getSummary(), newsResponse.getSummary());
        Assertions.assertEquals(news.getText(), newsResponse.getText());
    }

    @Test
    void shouldCreateNewsWhenUserIsAdmin() throws Exception {

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin@email.com");
        when(authentication.getAuthorities()).thenAnswer(invocation -> Collections.singletonList
            (new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(authentication.isAuthenticated()).thenReturn(true);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        byte[] imageBytes = createValidTestImage();
        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg"
            , MediaType.IMAGE_JPEG_VALUE, imageBytes);
        NewsRequestDto newsRequestDto = new NewsRequestDto();
        newsRequestDto.setTitle("Test Title");
        newsRequestDto.setSummary("Test Summary");
        newsRequestDto.setText("Test Text");
        MockMultipartFile newsPart = new MockMultipartFile("news", ""
            , MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(newsRequestDto));

        mockMvc.perform(multipart("/api/v1/news/create")
                .file(imageFile)
                .file(newsPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated());

        Assertions.assertEquals(1, newsRepository.count());
        News savedNews = newsRepository.findAll().getFirst();
        Assertions.assertEquals("Test Title", savedNews.getTitle());
        Assertions.assertEquals("Test Summary", savedNews.getSummary());
        Assertions.assertEquals("Test Text", savedNews.getText());
        Assertions.assertArrayEquals(imageBytes, savedNews.getImage(), "The image should be identical");
    }
}
