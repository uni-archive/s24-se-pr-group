package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.sql.Blob;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;
public class NewsInquiryDto {

    @NotNull(message = "Title must not be null")
    @Size(max = 100)
    private String title;

    @NotNull(message = "Summary must not be null")
    @Size(max = 500)
    private String summary;

    @NotNull(message = "Text must not be null")
    @Size(max = 10000)
    private String text;

    @NotNull(message = "An image must be provided")
    private Blob image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NewsInquiryDto that)) {
            return false;
        }
        return Objects.equals(title, that.title)
            && Objects.equals(summary, that.summary)
            && Objects.equals(text, that.text)
            && Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, summary, text, image);
    }

    @Override
    public String toString() {
        return "NewsInquiryDto{"
            + "title='" + title + '\''
            + ", summary='" + summary + '\''
            + ", text='" + text + '\''
                       + '}';
    }
}
