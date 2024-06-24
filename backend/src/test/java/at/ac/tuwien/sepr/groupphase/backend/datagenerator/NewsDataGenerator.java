package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.config.DataGenerationConfig;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ShowRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
@Profile("generateData")
@Component
public class NewsDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String TEST_NEWS_TITLE = "Lorem Ipsum";
    private static final String TEST_NEWS_SUMMARY = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur consectetur egestas arcu viverra facilisis.";
    private static final String TEST_NEWS_TEXT = "Curabitur consectetur egestas arcu viverra facilisis. Quisque dignissim, dolor ut imperdiet lacinia, nunc lectus dapibus tortor, iaculis luctus lacus eros non elit. Aenean dapibus, metus quis gravida euismod, tellus neque consectetur elit, volutpat elementum libero velit vel tellus. Aliquam erat volutpat. Aenean accumsan viverra quam et convallis. Curabitur eros ligula, placerat a vulputate et, auctor ut lectus. Ut auctor risus et massa condimentum, nec dignissim justo luctus. Sed tincidunt quam tellus, ac pulvinar lorem commodo ut. Morbi porttitor nisl nec nulla luctus, tempus maximus mauris accumsan. Sed dictum consequat hendrerit. Maecenas cursus bibendum sapien eu dictum. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas in tempor lectus.\n\nPraesent feugiat malesuada lacus et semper. Morbi iaculis viverra urna quis lacinia. Sed eget dui in odio tempus finibus eu eu nulla. Suspendisse nec posuere justo. Nunc tempus, arcu nec efficitur ullamcorper, lorem tortor fermentum dui, ac porttitor ex mi ac est. In imperdiet diam et tempor venenatis. Aenean ac orci magna.\n\nVivamus efficitur pretium nisi sit amet scelerisque. Cras vel odio mauris. Suspendisse ullamcorper porta enim, quis aliquet massa luctus ut. Nulla feugiat nunc non sollicitudin fermentum. Donec dolor est, commodo in orci et, euismod sodales purus. Duis faucibus sagittis cursus. Suspendisse condimentum massa ex, at mollis elit pulvinar vel. Nam ut arcu enim.";

    private final NewsRepository newsRepository;
    private final EventRepository eventRepository;
    private final ShowRepository showRepository;

    @Autowired
    private DataGenerationConfig dataGenerationConfig;

    public NewsDataGenerator(NewsRepository newsRepository, EventRepository eventRepository, ShowRepository showRepository) {
        this.newsRepository = newsRepository;
        this.eventRepository = eventRepository;
        this.showRepository = showRepository;
    }

    @PostConstruct
    private void generateNews() throws IOException {


        Event event1 = new Event();
        event1.setTitle("Furiosa: A Mad Max Saga");
        event1.setDescription("Die Mad Max Saga geht weiter mit Furiosa. Die Vorgeschichte zu Mad Max: Fury Road ist nicht weniger bombastisch und genial.");
        event1.setEventType(EventType.PLAY);
        event1.setDuration(7200L);

        eventRepository.save(event1);


        Event event2 = new Event();
        event2.setTitle("Deep Purple live");
        event2.setDescription("Sie begeistern seit Generationen: Die Kultrocker von Deep Purple! Erlebt über 50 Jahre Bandgeschichte live - TicketLine präsentiert Deep Purple.");
        event2.setEventType(EventType.CONCERT);
        event2.setDuration(7200L);

        eventRepository.save(event2);

        Event event3= new Event();
        event3.setTitle("Hildensaga - ein Königinnendrama");
        event3.setDescription("Endlich wieder im Theather! Entdecken Sie die unbezwingbare Schönheit und Stärke des Nordens – Brünhild, Königin von Island!");
        event3.setEventType(EventType.THEATER);
        event3.setDuration(7200L);

        eventRepository.save(event3);



        byte[] localImage = Files.readAllBytes(Paths.get("src/test/resources/furiosa.png"));

        List<Event> all = eventRepository.findAll();
        News news1 = News.NewsBuilder.aNews()
            .withTitle("Furiosa - der Sommerhit jetzt im Kino" )
            .withSummary("Die Mad Max Saga geht weiter mit Furiosa. Die Vorgeschichte zu Mad Max: Fury Road ist nicht weniger bombastisch und genial.")
            .withText("Als es zur Apokalypse kommt, wird die junge Furiosa (Anya Taylor-Joy) aus ihrer Heimat, dem Grünen Land, entführt und gerät in die Hände einer Biker-Bande, die dem Warlord Dementus (Chris Hemsworth) gehorchen. Während zwei Tyrannen um die Vorherrschaft über die Zitadelle, der einzigen bekannten Wasserquelle, kämpfen, muss Furiosa sich vielen Gefahren stellen, während sie einen Weg zurück nach Hause plant, der sie ausgerechnet durch das umkämpfte Ödland führt.\n" +

                "\n" +

                "Der australische Regisseur George Miller und der Drehbuchautor Nico Lathouris bescheren Fans weltweit mit „Furiosa“ das lang-ersehnte Prequel zu dem 2015 erschienenen Action-Hit „Mad Max: Fury Road“. Das postapokalyptische Action-Abenteuer ist außerdem ein Spin-off der „Mad-Max“-Reihe, die 1985 mit „Mad Max – Jenseits der Donnerkuppel“ begann. In „Mad Max: Fury Road“ stand der Kampf zwischen Imperator Furiosa (Charlize Theron) und Max Rockatansky (Tom Hardy) gegen den Kultführer Immortan Joe (Hugh Keays-Byrne) und seine Anhänger im Fokus. Hier bewies sich Furiosa bereits als furchtlose, willensstarke und moralische Anführerin. Der Action-Epos räumte damals sechs Oscars ab, etwa für das Beste Kostüm und den Besten Schnitt und war darüber hinaus für den besten Film und die beste Regie nominiert.")
            .withImage(localImage)
            .withPublishedAt(LocalDateTime.now().minusMonths(0))
            .withEvent(event1)
            .build();
        newsRepository.save(news1);

         localImage = Files.readAllBytes(Paths.get("src/test/resources/purple.png"));

        News news2 = News.NewsBuilder.aNews()
            .withTitle("Deep Purple live" )
            .withSummary("Sie begeistern seit Generationen: Die Kultrocker von Deep Purple! Erlebt über 50 Jahre Bandgeschichte live - TicketLine präsentiert Deep Purple.")
            .withText("Smoke on the water, Hush, Child in time, Space Truckin',... Die Liste an Rockklassikern, die diese Band uns geschenkt hat, würde sich noch ewig fortführen lassen. Nach zwei großartigen Shows im letzten Jahr kommen die Kultrocker von Deep Purple 2024 gleich nochmals zu uns in die Rock Republik!\n" +
                "\n" +
                "Mit im Gepäck haben Simon McBride, Ian Gillan, Roger Glover, Ian Paice und Don Airey natürlich jede Menge Kultsongs der Band und vielleicht ja auch einige neue Songs – Gerüchte über die Arbeit an einem neuen Album gibt's ja jede Menge. Eine Sache steht aber jedenfalls jetzt schon fest: Deep Purple sind bisher und werden mit Sicherheit auch in Zukunft ihren musikalischen Wurzeln treu bleiben. Immerhin haben sie in ihrer über 50-jährigen Bandgeschichte einen Sound geschaffen, der sie einfach unverwechselbar macht.\n" +
                "\n" +
                "Wir freuen uns sehr, mit euch und einer der größten Kultbands der Rockgeschichte gemeinsam zu rocken!" )
            .withImage(localImage)
            .withPublishedAt(LocalDateTime.now().minusMonths(0))
            .withEvent(event2)
            .build();
        newsRepository.save(news2);

        localImage = Files.readAllBytes(Paths.get("src/test/resources/brunhilde.png"));

        News news3= News.NewsBuilder.aNews()
            .withTitle("Hildensaga - ein Königinnendrama" )
            .withSummary("Endlich wieder im Theather! Entdecken Sie die unbezwingbare Schönheit und Stärke des Nordens – Brünhild, Königin von Island!")
            .withText("Brünhild, Königin von Island, ist berühmt für ihre Schönheit und Stärke und gilt als unbesiegbare Herrscherin des nordischen Eismeeres. Ihr Vater, Wotan, verspricht aber ihre Hand demjenigen, der sie in einem Dreikampf besiegen kann, und so verlieren zahlreiche Freier ihr Leben. Doch dann tritt ein bekanntes Gesicht auf, dem Brünhild einst für einen kurzen Moment verfallen war, bevor sie ihn zurückwies: Siegfried, der berühmte Drachentöter und Besitzer des Nibelungenschatzes, kehrt mit einer neuen Aufgabe im Gefolge des Burgunderkönigs Gunther zurück. Sein Ziel ist es, Gunther zu helfen, Brünhild zu besiegen, damit er Gunthers Schwester Kriemhild heiraten kann. Durch eine List gelingt das scheinbar Unmögliche: Brünhild wird besiegt, und es kommt zu dem verabredeten Austausch von Frau gegen Frau. Die Hochzeitsglocken von Burgund läuten doppelt.\n" +
                "\n" +
                "Während Brünhild, gegen ihren Willen von ihrer Heimat entfernt, vor den Altar gezwungen wird, verliebt sich Kriemhild gegen ihr eigenes Gelübde, den Männern abzuschwören, in Siegfried. Sie ahnt nichts von Siegfrieds kurzer Vergangenheit mit Brünhild. Doch Brünhild verweigert Gunther die Hochzeitsnacht und demütigt ihn vor dem Hof, was Siegfried erneut dazu bringt, unter der Tarnkappe einzugreifen. Als Kriemhild die Wahrheit über das Gefüge erkennt, verbündet sie sich unerwartet mit ihrer einstigen Rivalin." )
            .withImage(localImage)
            .withPublishedAt(LocalDateTime.now().minusMonths(0))
            .withEvent(event3)
            .build();
        newsRepository.save(news3);



        Random random = new Random();

            LOGGER.debug("generating {} news entries", dataGenerationConfig.newsAmount);
            for (int i = 1; i < dataGenerationConfig.newsAmount; i++) {
                LOGGER.info("generating news entry {} of {}", i, dataGenerationConfig.newsAmount);
                byte[] dummyImage = DummyImageGenerator.createDummyImage();
                News news = News.NewsBuilder.aNews()
                    .withTitle(TEST_NEWS_TITLE + " " + i )
                    .withSummary(TEST_NEWS_SUMMARY)
                    .withText(TEST_NEWS_TEXT)
                    .withImage(dummyImage)
                    .withPublishedAt(LocalDateTime.now().minusMonths(i))
                    .withEvent(all.get(random.nextInt(all.size())))
                    .build();
                LOGGER.debug("saving news {}", news);
                newsRepository.save(news);

        }
    }
}
