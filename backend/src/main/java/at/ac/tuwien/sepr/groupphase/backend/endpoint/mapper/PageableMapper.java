package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Mapper
public interface PageableMapper {

    @Named("toPageable")
    default org.springframework.data.domain.PageRequest toPageable(PageRequest pageRequest) {
        return org.springframework.data.domain.PageRequest.of(pageRequest.page(), pageRequest.size(),
            Sort.by(pageRequest.sort().orElse("id")));
    }
}
