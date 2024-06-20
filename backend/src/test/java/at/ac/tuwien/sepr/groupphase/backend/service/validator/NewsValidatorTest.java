package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class NewsValidatorTest {
/*
    private NewsValidator newsValidator;
    private News validNews;

    @BeforeEach
    public void setUp() throws IOException, SQLException {
        newsValidator = new NewsValidator();

        validNews = new News();
        validNews.setTitle("Valid Title");
        validNews.setSummary("Valid Summary");
        validNews.setText("Valid text");
        validNews.setPublishedAt(LocalDateTime.now());

        BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();
        Blob blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);

        validNews.setImage(blob);
    }

    @Test
    public void validateForValidNewsShouldNotThrowException() {
        assertDoesNotThrow(() -> newsValidator.validateForPublish(validNews));
    }

    @Test
    public void validateForTitleTooShortIsInvalid() {
        validNews.setTitle("A");
        ValidationException exception = assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(validNews));
        assertTrue(exception.getMessage().contains("Title must be between 3 and 100 characters long"));
    }

    @Test
    public void validateForTitleTooLongIsInvalid() {
        validNews.setTitle("A".repeat(101));
        ValidationException exception = assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(validNews));
        assertTrue(exception.getMessage().contains("Title must be between 3 and 100 characters long"));
    }

    @Test
    public void validateForSummaryTooShortIsInvalid() {
        validNews.setSummary("s");
        ValidationException exception = assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(validNews));
        assertTrue(exception.getMessage().contains("Summary must be between 3 and 500 characters long"));
    }

    @Test
    public void validateForSummaryTooLongIsInvalid() {
        validNews.setSummary("A".repeat(501));
        ValidationException exception = assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(validNews));
        assertTrue(exception.getMessage().contains("Summary must be between 3 and 500 characters long"));
    }

    @Test
    public void validateForTextTooShortIsInvalid() {
        validNews.setText("A");
        ValidationException exception = assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(validNews));
        assertTrue(exception.getMessage().contains("Text must be between 3 and 10000 characters long"));
    }

    @Test
    public void validateForTextTooLongIsInvalid() {
        validNews.setText("A".repeat(10001));
        ValidationException exception = assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(validNews));
        assertTrue(exception.getMessage().contains("Text must be between 3 and 10000 characters long"));
    }

    @Test
    public void validateForImageNullThrowsException() {
        validNews.setImage(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(validNews));
        assertTrue(exception.getMessage().contains("An image must be provided"));
    }

    @Test
    public void validateForImageTooLargeThrowsException() throws SQLException {
        byte[] largeImageBytes = new byte[11 * 1024 * 1024];
        Blob largeBlob = new javax.sql.rowset.serial.SerialBlob(largeImageBytes);
        validNews.setImage(largeBlob);

        ValidationException exception = assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(validNews));
        assertTrue(exception.getMessage().contains("Image file size must not exceed 10 MB"));
    }

    @Test
    public void validateForImageWidthTooSmallThrowsException() throws IOException, SQLException {
        BufferedImage smallWidthImage = new BufferedImage(199, 500, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(smallWidthImage, "jpg", stream);
        byte[] imageBytes = stream.toByteArray();
        Blob blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
        validNews.setImage(blob);

        ValidationException exception = assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(validNews));
        assertTrue(exception.getMessage().contains("Image width must be between 200 and 1000 pixels"));
    }

    @Test
    public void validateForImageHeightTooSmallThrowsException() throws IOException, SQLException {
        BufferedImage smallHeightImage = new BufferedImage(500, 199, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(smallHeightImage, "jpg", stream);
        byte[] imageBytes = stream.toByteArray();
        Blob blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
        validNews.setImage(blob);

        ValidationException exception = assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(validNews));
        assertTrue(exception.getMessage().contains("Image height must be between 200 and 1000 pixels"));
    }

    @Test
    public void validateForImageWidthTooLargeThrowsException() throws IOException, SQLException {
        BufferedImage largeWidthImage = new BufferedImage(1001, 500, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(largeWidthImage, "jpg", stream);
        byte[] imageBytes = stream.toByteArray();
        Blob blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
        validNews.setImage(blob);

        ValidationException exception = assertThrows(ValidationException.class, () -> newsValidator.validateForPublish(validNews));
        assertTrue(exception.getMessage().contains("Image width must be between 200 and 1000 pixels"));
    }
*/
 }