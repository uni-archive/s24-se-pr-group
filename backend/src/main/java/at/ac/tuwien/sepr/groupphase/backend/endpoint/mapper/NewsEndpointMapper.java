package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.NewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsResponseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleNewsResponseDto;
import org.mapstruct.Mapper;

@Mapper
public interface NewsEndpointMapper extends BaseResponseMapper<NewsDto, NewsResponseDto> {
    NewsDto toDto(NewsRequestDto requestDto);

    SimpleNewsResponseDto toSimpleResponse(NewsDto newsDto);
}
