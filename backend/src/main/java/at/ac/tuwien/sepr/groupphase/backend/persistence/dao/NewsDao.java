package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;
import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.mapper.NewsMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class NewsDao extends AbstractDao<News, NewsDto> {
    public NewsDao(NewsRepository repository, @Qualifier("backendNewsMapperImpl") NewsMapper mapper) {
        super(repository, mapper);
    }
}
