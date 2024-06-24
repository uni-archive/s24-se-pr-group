package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class NewsValidatorTest {

    private NewsValidator newsValidator;
    private EventRepository eventRepository;

    @BeforeEach
    public void setup() {
        eventRepository = Mockito.mock(EventRepository.class);
        newsValidator = new NewsValidator(eventRepository);
    }

    @Test
    public void testValidateForPublish_ValidData() throws ValidationException, IOException {
        NewsDto newsDto = createValidNewsDto();
        when(eventRepository.existsById(newsDto.getEventDto().getId())).thenReturn(true);
        newsValidator.validateForPublish(newsDto);
    }

    @Test
    public void testValidateForPublish_TitleTooShort() throws IOException {
        NewsDto newsDto = createValidNewsDto();
        newsDto.setTitle("A");
        assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(newsDto));
    }

    @Test
    public void testValidateForPublish_TitleTooLong() throws IOException {
        NewsDto newsDto = createValidNewsDto();
        newsDto.setTitle("A".repeat(101));
        assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(newsDto));
    }

    @Test
    public void testValidateForPublish_SummaryTooShort() throws IOException {
        NewsDto newsDto = createValidNewsDto();
        newsDto.setSummary("A");
        assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(newsDto));
    }

    @Test
    public void testValidateForPublish_SummaryTooLong() throws IOException {
        NewsDto newsDto = createValidNewsDto();
        newsDto.setSummary("A".repeat(501));
        assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(newsDto));
    }

    @Test
    public void testValidateForPublish_TextTooShort() throws IOException {
        NewsDto newsDto = createValidNewsDto();
        newsDto.setText("A");
        assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(newsDto));
    }

    @Test
    public void testValidateForPublish_TextTooLong() throws IOException {
        NewsDto newsDto = createValidNewsDto();
        newsDto.setText("A".repeat(10001));
        assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(newsDto));
    }

    @Test
    public void testValidateForPublish_NoImage() throws IOException {
        NewsDto newsDto = createValidNewsDto();
        newsDto.setImage(null);
        assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(newsDto));
    }

    @Test
    public void testValidateForPublish_InvalidImage() throws IOException {
        NewsDto newsDto = createValidNewsDto();
        newsDto.setImage(new byte[]{1, 2, 3});
        assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(newsDto));
    }

    @Test
    public void testValidateForPublish_ImageTooLarge() throws IOException {
        NewsDto newsDto = createValidNewsDto();
        newsDto.setImage(new byte[10 * 1024 * 1024 + 1]); // 10 MB + 1 Byte
        assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(newsDto));
    }

    @Test
    public void testValidateForPublish_ImageTooSmall() throws IOException {
        NewsDto newsDto = createValidNewsDto();
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        newsDto.setImage(imageToByteArray(image));
        assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(newsDto));
    }

    @Test
    public void testValidateForPublish_NoEvent() throws IOException {
        NewsDto newsDto = createValidNewsDto();
        newsDto.setEventDto(null);
        assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(newsDto));
    }

    @Test
    public void testValidateForPublish_EventNotExists() throws IOException {
        NewsDto newsDto = createValidNewsDto();
        when(eventRepository.existsById(newsDto.getEventDto().getId())).thenReturn(false);
        assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(newsDto));
    }

    private NewsDto createValidNewsDto() throws IOException {
        NewsDto newsDto = new NewsDto();
        newsDto.setTitle("Valid Title");
        newsDto.setSummary("Valid Summary");
        newsDto.setText("Valid Text");
        newsDto.setImage(createValidImage());
        newsDto.setEventDto(new EventDto().setId(1L));
        return newsDto;
    }

    private byte[] createValidImage() throws IOException {
        BufferedImage bufferedImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        return imageToByteArray(bufferedImage);
    }

    private byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
