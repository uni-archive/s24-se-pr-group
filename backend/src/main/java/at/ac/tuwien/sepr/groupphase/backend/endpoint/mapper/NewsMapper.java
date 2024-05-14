package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import java.sql.Blob;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper
public interface NewsMapper {

    @Named("simpleNews")
    SimpleNewsDto newsToSimpleNewsDto(News news);

    /**
     * This is necessary since the SimpleNewsDto misses the text property and the collection mapper can't handle missing
     * fields.
     **/
    @IterableMapping(qualifiedByName = "simpleNews")
    List<SimpleNewsDto> newsToSimpleNewsDto(List<News> news);

    DetailedNewsDto newsToDetailedNewsDto(News news);

    News detailedNewsDtoToNews(DetailedNewsDto detailedNewsDto);

    News newsInquiryDtoToNews(NewsInquiryDto newsInquiryDto);

    NewsInquiryDto newsToNewsInquiryDto(News news);


    default byte[] blobToByteArray(java.sql.Blob blob) {
        try {
            return blob.getBytes(1, (int) blob.length());
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }

    default Blob byteArrayToBlob(byte[] bytes) {
        try {
            return new javax.sql.rowset.serial.SerialBlob(bytes);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
