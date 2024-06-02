package at.ac.tuwien.sepr.groupphase.backend.dto;

import java.time.LocalDateTime;

public class NewsDto implements AbstractDto {
    private Long id;
    private String title;
    private String summary;
    private String text;
    private LocalDateTime publishedAt;
    private byte[] image;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "NewsDto{"
            + "id=" + id
            + ", title='" + title + '\''
            + ", summary='" + summary + '\''
            + ", text='" + text + '\''
            + ", publishedAt=" + publishedAt
            + ", image=" + (image != null ? "present" : "null")
            + '}';
    }
}
