package at.ac.tuwien.sepr.groupphase.backend.persistence.specification;

import at.ac.tuwien.sepr.groupphase.backend.dto.TicketSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TicketSpecification {

    public Specification<Ticket> getTickets(TicketSearchDto ticketSearchDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            addNamesToPredicates(ticketSearchDto, root, criteriaBuilder, predicates);
            addShowToPredicats(ticketSearchDto, root, criteriaBuilder, predicates);
            addReservedToPredicate(ticketSearchDto, root, criteriaBuilder, predicates);
            addValidToPredicate(ticketSearchDto, root, criteriaBuilder, predicates);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addNamesToPredicates(
        TicketSearchDto ticketSearchDto,
        Root<Ticket> root,
        CriteriaBuilder criteriaBuilder,
        List<Predicate> predicates) {
        if (StringUtils.isNotBlank(ticketSearchDto.firstName())) {
            predicates.add(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("order").get("customer").get("firstName")),
                    "%" + ticketSearchDto.firstName().toLowerCase() + "%"));
        }
        if (StringUtils.isNotBlank(ticketSearchDto.familyName())) {
            predicates.add(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("order").get("customer").get("familyName")),
                    "%" + ticketSearchDto.familyName().toLowerCase() + "%"));
        }
    }

    private void addShowToPredicats(
        TicketSearchDto ticketSearchDto,
        Root<Ticket> root,
        CriteriaBuilder criteriaBuilder,
        List<Predicate> predicates) {
        if (ticketSearchDto.showId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("show").get("id"), ticketSearchDto.showId()));
        }
    }

    private void addReservedToPredicate(
        TicketSearchDto ticketSearchDto,
        Root<Ticket> root,
        CriteriaBuilder criteriaBuilder,
        List<Predicate> predicates) {
        if (ticketSearchDto.reservedOnly()) {
            predicates.add(criteriaBuilder.isTrue(root.get("reserved")));
        }
    }

    private void addValidToPredicate(
        TicketSearchDto ticketSearchDto,
        Root<Ticket> root,
        CriteriaBuilder criteriaBuilder,
        List<Predicate> predicates) {
        if (ticketSearchDto.valid()) {
            predicates.add(criteriaBuilder.isTrue(root.get("valid")));
        } else {
            predicates.add(criteriaBuilder.isFalse(root.get("valid")));
        }
    }
}
