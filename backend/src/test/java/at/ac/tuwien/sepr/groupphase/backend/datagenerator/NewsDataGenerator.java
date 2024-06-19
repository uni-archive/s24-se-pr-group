package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.config.DataGenerationConfig;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.NewsRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final String TEST_NEWS_TITLE = "Lorem Ipsum";
    private static final String TEST_NEWS_SUMMARY = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur " +
        "consectetur egestas arcu viverra facilisis.";
    private static final String TEST_NEWS_TEXT = "Curabitur consectetur egestas arcu viverra facilisis. Quisque " +
        "dignissim, dolor ut imperdiet lacinia, nunc lectus dapibus tortor, iaculis luctus lacus eros non elit. Aenean " +
        "dapibus, metus quis gravida euismod, tellus neque consectetur elit, volutpat elementum libero velit vel tellus." +
        " Aliquam erat volutpat. Aenean accumsan viverra quam et convallis. Curabitur eros ligula, placerat a vulputate " +
        "et, auctor ut lectus. Ut auctor risus et massa condimentum, nec dignissim justo luctus. Sed tincidunt quam " +
        "tellus, ac pulvinar lorem commodo ut. Morbi porttitor nisl nec nulla luctus, tempus maximus mauris accumsan." +
        " Sed dictum consequat hendrerit. Maecenas cursus bibendum sapien eu dictum. Lorem ipsum dolor sit amet," +
        " consectetur adipiscing elit. Maecenas in tempor lectus.\n" +
        "\n" +
        "Praesent feugiat malesuada lacus et semper. Morbi iaculis viverra urna quis lacinia. Sed eget dui in odio" +
        " tempus finibus eu eu nulla. Suspendisse nec posuere justo. Nunc tempus, arcu nec efficitur ullamcorper, lorem" +
        " tortor fermentum dui, ac porttitor ex mi ac est. In imperdiet diam et tempor venenatis. Aenean ac orci magna.\n" +
        "\n" +
        "Vivamus efficitur pretium nisi sit amet scelerisque. Cras vel odio mauris. Suspendisse ullamcorper porta enim," +
        " quis aliquet massa luctus ut. Nulla feugiat nunc non sollicitudin fermentum. Donec dolor est, commodo in orci " +
        "et, euismod sodales purus. Duis faucibus sagittis cursus. Suspendisse condimentum massa ex, at mollis elit " +
        "pulvinar vel. Nam ut arcu enim.";

    private final NewsRepository newsRepository;

    @Autowired
    private DataGenerationConfig dataGenerationConfig;

    public NewsDataGenerator(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @PostConstruct
    private void generateNews() throws IOException {
        if (newsRepository.findAll().size() > 0) {
            LOGGER.debug("news already generated");
        } else {
            LOGGER.debug("generating {} news entries", dataGenerationConfig.newsAmount);
            for (int i = 0; i < dataGenerationConfig.newsAmount; i++) {
                LOGGER.info("generating news entry {} of {}", i, dataGenerationConfig.newsAmount);
                byte[] dummyImage = DummyImageGenerator.createDummyImage();
                News news = News.NewsBuilder.aNews()
                    .withTitle(TEST_NEWS_TITLE + " " + i)
                    .withSummary(TEST_NEWS_SUMMARY)
                    .withText(TEST_NEWS_TEXT)
                    .withImage(dummyImage)
                    .withPublishedAt(LocalDateTime.now().minusMonths(i))
                    .build();
                LOGGER.debug("saving news {}", news);
                newsRepository.save(news);
            }
        }
    }

}
