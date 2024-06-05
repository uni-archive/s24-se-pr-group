package at.ac.tuwien.sepr.groupphase.backend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NewsDto implements AbstractDto {
    private Long id;
    private String title;
    private String summary;
    private String text;
    private LocalDateTime publishedAt;
    private byte[] image;
    private List<ApplicationUserDto> users;

    public NewsDto() {
        this.users = new ArrayList<>();
    }

    // Getter und Setter für id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter und Setter für title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter und Setter für summary
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    // Getter und Setter für text
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // Getter und Setter für publishedAt
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    // Getter und Setter für image
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    // Getter und Setter für users
    public List<ApplicationUserDto> getUsers() {
        return users;
    }

    public void setUsers(List<ApplicationUserDto> users) {
        this.users = users;
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
            + ", users=" + users
            + '}';
    }
}
