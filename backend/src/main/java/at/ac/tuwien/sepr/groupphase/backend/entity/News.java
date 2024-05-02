package at.ac.tuwien.sepr.groupphase.backend.entity;
import jakarta.persistence.*;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class News extends AEntity {

    @Column(name = "TITLE")
    private String title;

    @Column(name="dateTime")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateTime;

    @Column(name = "DESCRIPTION")
    private String description;

    @Lob
    @Column(name = "NEWS_IMAGE")
    private Blob newsImage;

    @ManyToMany(mappedBy = "news")
    private List<ApplicationUser> users;

    @ManyToOne
    @JoinColumn(name="EVENT_ID")
    private Event event;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Blob getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(Blob backgroundImage) {
        this.newsImage = backgroundImage;
    }

    public List<ApplicationUser> getUsers() {
        return users;
    }

    public void setUsers(List<ApplicationUser> users) {
        this.users = users;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        News news = (News) o;
        return Objects.equals(title, news.title) && Objects.equals(dateTime, news.dateTime) && Objects.equals(description, news.description) && Objects.equals(newsImage, news.newsImage) && Objects.equals(users, news.users) && Objects.equals(event, news.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, dateTime, description, newsImage, users, event);
    }

    @Override
    public String toString() {
        return "News{" +
            "title='" + title + '\'' +
            ", dateTime=" + dateTime +
            ", description='" + description + '\'' +
            ", newsImage=" + newsImage +
            ", users=" + users +
            ", event=" + event +
            '}';
    }
}
