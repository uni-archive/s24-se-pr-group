/*package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.News;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;

public interface NewsService {


    List<News> findAll();



    News findOne(Long id);


    News publishNews(News news) throws ValidationException;

}
*/

package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;

import java.io.IOException;
import java.util.List;

public interface NewsService {
    NewsDto getNewsById(Long id) throws EntityNotFoundException;

    NewsDto createNews(NewsDto newsDto) throws IOException, ValidationException;

    List<NewsDto> getAllNews();

    List<NewsDto> getUnseenNews() throws EntityNotFoundException;
}



