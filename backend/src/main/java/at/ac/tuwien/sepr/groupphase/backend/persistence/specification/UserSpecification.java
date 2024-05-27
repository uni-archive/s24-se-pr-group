package at.ac.tuwien.sepr.groupphase.backend.persistence.specification;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Location;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecification {

    public Specification<ApplicationUser> getUserSpecification(ApplicationUserSearchDto userSearchDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(userSearchDto.familyName())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("familyName")),
                    "%" + userSearchDto.familyName().toLowerCase() + "%"));
            }
            if (StringUtils.isNotBlank(userSearchDto.firstName())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")),
                    "%" + userSearchDto.firstName().toLowerCase() + "%"));
            }
            if (StringUtils.isNotBlank(userSearchDto.email())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                    "%" + userSearchDto.email().toLowerCase() + "%"));
            }
            predicates.add(criteriaBuilder.equal(root.get("accountLocked"), userSearchDto.isLocked()));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


}
