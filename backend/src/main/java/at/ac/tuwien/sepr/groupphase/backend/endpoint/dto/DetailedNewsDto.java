package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import org.springframework.web.multipart.MultipartFile;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.Objects;
public class DetailedNewsDto extends SimpleNewsDto {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "DetailedMessageDto{"
            + "text='" + text + '\''
            + '}';
    }


    public static final class DetailedNewsDtoBuilder {
        private Long id;
        private LocalDateTime publishedAt;
        private String text;
        private String title;
        private String summary;

        private byte[] image;



        private DetailedNewsDtoBuilder() {
        }

        public static at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto.DetailedNewsDtoBuilder aDetailedNewsDDto() {
            return new at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto.DetailedNewsDtoBuilder();
        }

        public at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto.DetailedNewsDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto.DetailedNewsDtoBuilder withPublishedAt(LocalDateTime publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto.DetailedNewsDtoBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto.DetailedNewsDtoBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto.DetailedNewsDtoBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto.DetailedNewsDtoBuilder withImage(byte[] image) {
            this.image = image;
            return this;
        }


        public at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto build() {
            at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto
                detailedNewsDto = new at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto();
            detailedNewsDto.setId(id);
            detailedNewsDto.setPublishedAt(publishedAt);
            detailedNewsDto.setText(text);
            detailedNewsDto.setTitle(title);
            detailedNewsDto.setSummary(summary);
            detailedNewsDto.setImage(image);
            return detailedNewsDto;
        }
    }
}