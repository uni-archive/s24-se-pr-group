package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.LocationSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationSearchRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationSummaryResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Mapper(uses = {AddressResponseMapper.class, PageableMapper.class}, componentModel = "spring")
public interface LocationResponseMapper extends BaseResponseMapper<LocationDto, LocationResponse> {

    @Mapping(source = "addressCreateRequest", target = "address")
    @Mapping(source = "hallPlanId", target = "hallPlan.id")
    LocationDto createRequestToDto(LocationCreateRequest createRequest);


    @Mapping(source = "pageRequest", target = "pageable", qualifiedByName = "toPageable")
    LocationSearch toSearch(LocationSearchRequest searchRequest);

    LocationSummaryResponse toSummaryResponse(LocationDto dto);
}
