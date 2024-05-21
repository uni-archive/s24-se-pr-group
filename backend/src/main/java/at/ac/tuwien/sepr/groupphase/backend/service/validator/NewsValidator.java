package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NewsValidator {

    public void validateForPublish(News news) throws ValidationException {


        List<String> errors = new ArrayList<>();

        if (news.getTitle() == null || news.getTitle().length() < 3 || news.getTitle().length() > 100) {
            errors.add("Titel muss zwischen 3 und 100 Zeichen lang sein");
        }
        if (news.getSummary() == null || news.getSummary().trim().length() < 3 || news.getSummary().trim().length() > 500) {
            errors.add("Zusammenfassung muss zwischen 3 und 500 Zeichen lang sein");
        }

        if (news.getText() == null || news.getText().trim().length() < 3 || news.getText().trim().length() > 10000) {
            errors.add("Text muss zwischen 3 und 10000 Zeichen lang sein");
        }

        if (news.getImage() == null) {
            errors.add("Ein Bild muss bereitgestellt werden");
        }

        endValidation(errors);
    }

    private void endValidation(List<String> errors) throws ValidationException {
        if (!errors.isEmpty()) {
            String message = "Validation Error: " + String.join(", ", errors);
            throw new ValidationException(message);
        }
    }


}
