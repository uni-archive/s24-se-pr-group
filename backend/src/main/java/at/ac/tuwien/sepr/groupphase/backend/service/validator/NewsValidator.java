package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class NewsValidator {
    private static final int MIN_WIDTH = 400;
    private static final int MAX_WIDTH = 2000;
    private static final int MIN_HEIGHT = 200;
    private static final int MAX_HEIGHT = 1000;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public void validateForPublish(NewsDto newsDto) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (newsDto.getTitle() == null || newsDto.getTitle().length() < 3 || newsDto.getTitle().length() > 100) {
            errors.add("Title must be between 3 and 100 characters long");
        }
        if (newsDto.getSummary() == null || newsDto.getSummary().trim().length() < 3 || newsDto.getSummary().trim().length() > 500) {
            errors.add("Summary must be between 3 and 500 characters long");
        }
        if (newsDto.getText() == null || newsDto.getText().trim().length() < 3 || newsDto.getText().trim().length() > 10000) {
            errors.add("Text must be between 3 and 10000 characters long");
        }
        if (newsDto.getImage() == null) {
            errors.add("An image must be provided");
        } else {
            validateImage(newsDto.getImage(), errors);
        }

        endValidation(errors);
    }

    private void validateImage(byte[] imageBytes, List<String> errors) {
        try {
            if (imageBytes.length > MAX_FILE_SIZE) {
                errors.add("Image file size must not exceed 10 MB");
            }

            ByteArrayInputStream stream = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(stream);

            if (image == null) {
                errors.add("Image file is not valid");
                return;
            }

            int width = image.getWidth();
            int height = image.getHeight();

            if (width < MIN_WIDTH || width > MAX_WIDTH) {
                errors.add("Image width must be between " + MIN_WIDTH + " and " + MAX_WIDTH + " pixels");
            }
            if (height < MIN_HEIGHT || height > MAX_HEIGHT) {
                errors.add("Image height must be between " + MIN_HEIGHT + " and " + MAX_HEIGHT + " pixels");
            }
        } catch (IOException e) {
            errors.add("An error occurred while processing the image data");
        }
    }

    private void endValidation(List<String> errors) throws ValidationException {
        if (!errors.isEmpty()) {
            String message = "Validation Error: " + String.join(", ", errors);
            throw new ValidationException(message);
        }
    }
}

/*
package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class NewsValidator {
  private static final int MIN_WIDTH = 200;
    private static final int MAX_WIDTH = 1000;
    private static final int MIN_HEIGHT = 200;
    private static final int MAX_HEIGHT = 1000;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public void validateForPublish(News news) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (news.getTitle() == null || news.getTitle().length() < 3 || news.getTitle().length() > 100) {
            errors.add("Title must be between 3 and 100 characters long");
        }
        if (news.getSummary() == null || news.getSummary().trim().length() < 3 || news.getSummary().trim().length() > 500) {
            errors.add("Summary must be between 3 and 500 characters long");
        }
        if (news.getText() == null || news.getText().trim().length() < 3 || news.getText().trim().length() > 10000) {
            errors.add("Text must be between 3 and 10000 characters long");
        }
        if (news.getImage() == null) {
            errors.add("An image must be provided");
        } else {
            validateImage(news.getImage(), errors);
        }

        endValidation(errors);
    }

    private void validateImage(Blob imageBlob, List<String> errors) {
        try {
            long fileSize = imageBlob.length();
            if (fileSize > MAX_FILE_SIZE) {
                errors.add("Image file size must not exceed 10 MB");
            }

            byte[] imageBytes = imageBlob.getBytes(1, (int) fileSize);
            ByteArrayInputStream stream = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(stream);

            if (image == null) {
                errors.add("Image file is not valid");
                return;
            }

            int width = image.getWidth();
            int height = image.getHeight();

            if (width < MIN_WIDTH || width > MAX_WIDTH) {
                errors.add("Image width must be between " + MIN_WIDTH + " and " + MAX_WIDTH + " pixels");
            }
            if (height < MIN_HEIGHT || height > MAX_HEIGHT) {
                errors.add("Image height must be between " + MIN_HEIGHT + " and " + MAX_HEIGHT + " pixels");
            }
        } catch (IOException | SQLException e) {
            errors.add("An error occurred while processing the image data");
        }
    }

    private void endValidation(List<String> errors) throws ValidationException {
        if (!errors.isEmpty()) {
            String message = "Validation Error: " + String.join(", ", errors);
            throw new ValidationException(message);
        }
    }

  */
