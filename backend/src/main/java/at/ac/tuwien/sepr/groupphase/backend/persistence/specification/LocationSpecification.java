package at.ac.tuwien.sepr.groupphase.backend.persistence.specification;

import at.ac.tuwien.sepr.groupphase.backend.dto.LocationSearch;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class LocationSpecification {

    protected static final String ADDRESS = "address";

    public Specification<Location> getLocations(LocationSearch locationSearch) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            addNameToPredicates(locationSearch, root, criteriaBuilder, predicates);
            addAddressToPredicates(locationSearch, root, criteriaBuilder, predicates);
            addWithUpcomingShowsToPredicates(locationSearch, root, criteriaBuilder, predicates);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addNameToPredicates(
        LocationSearch locationSearch,
        Root<Location> root,
        CriteriaBuilder criteriaBuilder,
        List<Predicate> predicates) {
        if (StringUtils.isNotBlank(locationSearch.getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                "%" + locationSearch.getName().toLowerCase() + "%"));
        }
    }

    private void addAddressToPredicates(
        LocationSearch locationSearch,
        Root<Location> root,
        CriteriaBuilder criteriaBuilder,
        List<Predicate> predicates) {
        if (StringUtils.isNotBlank(locationSearch.getAddressSearch().city())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(ADDRESS).get("city")),
                "%" + locationSearch.getAddressSearch().city().toLowerCase() + "%"));
        }
        if (StringUtils.isNotBlank(locationSearch.getAddressSearch().street())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(ADDRESS).get("street")),
                "%" + locationSearch.getAddressSearch().street().toLowerCase() + "%"));
        }
        if (StringUtils.isNotBlank(locationSearch.getAddressSearch().postalCode())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(ADDRESS).get("zip")),
                "%" + locationSearch.getAddressSearch().postalCode().toLowerCase() + "%"));
        }
        if (StringUtils.isNotBlank(locationSearch.getAddressSearch().country())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(ADDRESS).get("country")),
                "%" + locationSearch.getAddressSearch().country().toLowerCase() + "%"));
        }
    }

    private void addWithUpcomingShowsToPredicates(
        LocationSearch locationSearch,
        Root<Location> root,
        CriteriaBuilder criteriaBuilder,
        List<Predicate> predicates) {
        if (locationSearch.isWithUpComingShows()) {
            Join<Location, Show> shows = root.join("shows", JoinType.INNER);
            predicates.add(criteriaBuilder.greaterThan(shows.get("dateTime"), LocalDateTime.now()));
        }
    }
}
