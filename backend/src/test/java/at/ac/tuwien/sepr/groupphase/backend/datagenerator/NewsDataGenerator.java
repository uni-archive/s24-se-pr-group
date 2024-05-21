package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.NewsRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Profile("generateData")
@Component
public class NewsDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_NEWS_TO_GENERATE = 5;
    private static final String TEST_NEWS_TITLE = "Titel";
    private static final String TEST_NEWS_SUMMARY = "Zusammenfassung des Texts";
    private static final String TEST_NEWS_TEXT = "Text";

    private final NewsRepository newsRepository;

    public NewsDataGenerator(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @PostConstruct
    private void generateNews() throws IOException {
        if (newsRepository.findAll().size() > 0) {
            LOGGER.debug("news already generated");
        } else {
            LOGGER.debug("generating {} news entries", NUMBER_OF_NEWS_TO_GENERATE);
            for (int i = 0; i < NUMBER_OF_NEWS_TO_GENERATE; i++) {
                byte[] dummyImage = DummyImageGenerator.createDummyImage();
                News news = News.NewsBuilder.aNews()
                    .withTitle(TEST_NEWS_TITLE + " " + i)
                    .withSummary(TEST_NEWS_SUMMARY + " " + i)
                    .withText(TEST_NEWS_TEXT + " " + i)
                    .withImage(dummyImage)
                    .withPublishedAt(LocalDateTime.now().minusMonths(i))
                    .build();
                LOGGER.debug("saving news {}", news);
                newsRepository.save(news);
            }
        }
    }

}
