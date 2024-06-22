package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class NewsValidator {
    private static final int MIN_WIDTH = 400;
    private static final int MAX_WIDTH = 2000;
    private static final int MIN_HEIGHT = 200;
    private static final int MAX_HEIGHT = 1000;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    public NewsValidator(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void validateForPublish(NewsDto newsDto) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (newsDto.getTitle() == null || newsDto.getTitle().length() < 3 || newsDto.getTitle().length() > 100) {
            errors.add("Der Titel muss zwischen 3 und 100 Zeichen lang sein");
        }
        if (newsDto.getSummary() == null || newsDto.getSummary().length() < 3 || newsDto.getSummary().length() > 500) {
            errors.add("Die Zusammenfassung muss zwischen 3 und 500 Zeichen lang sein");
        }
        if (newsDto.getText() == null || newsDto.getText().length() < 3 || newsDto.getText().length() > 10000) {
            errors.add("Der Text muss zwischen 3 und 10000 Zeichen lang sein");
        }
        if (newsDto.getImage() == null) {
            errors.add("Ein Bild muss bereitgestellt werden");
        } else {
            validateImage(newsDto.getImage(), errors);
        }
        if (newsDto.getEventDto() == null) {
            errors.add("Ein Event muss bereitgestellt werden");
        } else {
            validateEvent(newsDto.getEventDto(), errors);
        }

        endValidation(errors);
    }

    private void validateImage(byte[] imageBytes, List<String> errors) {
        if (imageBytes.length > MAX_FILE_SIZE) {
            errors.add("Die Bilddatei darf 10 MB nicht überschreiten");
            return;
        }

        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(imageBytes);
             ImageInputStream inputStream = ImageIO.createImageInputStream(byteStream)) {

            Iterator<ImageReader> readers = ImageIO.getImageReaders(inputStream);
            if (!readers.hasNext()) {
                errors.add("Die Bilddatei ist nicht gültig");
                return;
            }

            ImageReader reader = readers.next();
            reader.setInput(inputStream);
            BufferedImage image = reader.read(0);

            if (image == null) {
                errors.add("Die Bilddatei ist nicht gültig");
                return;
            }

            int width = image.getWidth();
            int height = image.getHeight();

            if (width < MIN_WIDTH || width > MAX_WIDTH) {
                errors.add("Die Bildbreite muss zwischen " + MIN_WIDTH + " und " + MAX_WIDTH + " Pixel liegen");
            }
            if (height < MIN_HEIGHT || height > MAX_HEIGHT) {
                errors.add("Die Bildhöhe muss zwischen " + MIN_HEIGHT + " und " + MAX_HEIGHT + " Pixel liegen");
            }
        } catch (IOException e) {
            errors.add("Beim Verarbeiten der Bilddaten ist ein Fehler aufgetreten: " + e.getMessage());
        }
    }

    private void validateEvent(EventDto eventDto, List<String> errors) {
        if (eventDto.getId() <= 0 || !eventRepository.existsById(eventDto.getId())) {
            errors.add("Die Veranstaltungs-ID muss existieren");
        }
    }

    private void endValidation(List<String> errors) throws ValidationException {
        if (!errors.isEmpty()) {
            String message = "Validierungsfehler: " + String.join(", ", errors);
            throw new ValidationException(message);
        }
    }
}
