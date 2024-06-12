package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class News extends AbstractEntity {

    @Column(name = "TITLE")
    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, name = "published_at")
    private LocalDateTime publishedAt;

    @Column(nullable = false, length = 500)
    private String summary;

    @Column(nullable = false, length = 10000)
    private String text;

    @Lob
    @Column(name = "IMAGE")
    private byte[] image;

    @ManyToMany(mappedBy = "news")
    private List<ApplicationUser> users;

    public List<ApplicationUser> getUsers() {
        return users;
    }

    public void setUsers(List<ApplicationUser> users) {
        this.users = users;
    }

    @ManyToOne
    @JoinColumn(name = "EVENT_ID")
    private Event event;

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof News news)) {
            return false;
        }
        return Objects.equals(getId(), news.getId()) && Objects.equals(publishedAt, news.publishedAt) && Objects.equals(title, news.title)
            && Objects.equals(summary, news.summary) && Objects.equals(text, news.text) && Objects.equals(image, news.image) && Objects.equals(users, news.users) && Objects.equals(event, news.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), publishedAt, title, summary, text, image, users, event);
    }

    @Override
    public String toString() {
        return "News{" + "publishedAt=" + publishedAt + ", title='" + title + '\'' + ", summary='" + summary + '\'' + ", text='" + text + '\'' + ", users=" + users + ", event=" + event + '}';
    }

    public static final class NewsBuilder {
        private Long id;
        private LocalDateTime publishedAt;
        private String title;
        private String summary;
        private String text;
        private byte[] image;
        private Event event;
        private NewsBuilder() {
        }

        public static NewsBuilder aNews() {
            return new NewsBuilder();
        }

        public NewsBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public NewsBuilder withPublishedAt(LocalDateTime publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public NewsBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public NewsBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public NewsBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public NewsBuilder withImage(byte[] image) {
            this.image = image;
            return this;
        }

        public NewsBuilder withEvent(Event event) {
            this.event = event;
            return this;
        }

        public News build() {
            News news = new News();
            news.setId(id);
            news.setPublishedAt(publishedAt);
            news.setTitle(title);
            news.setSummary(summary);
            news.setText(text);
            news.setImage(image);
            news.setEvent(event);
            return news;
        }
    }
}
