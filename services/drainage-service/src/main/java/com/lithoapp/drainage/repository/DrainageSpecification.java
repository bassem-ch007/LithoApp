package com.lithoapp.drainage.repository;

import com.lithoapp.drainage.dto.DrainageFilterRequest;
import com.lithoapp.drainage.entity.Drainage;
import com.lithoapp.drainage.enums.DrainageStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds a JPA Specification from a DrainageFilterRequest.
 * Each filter is applied only when the corresponding field is non-null.
 */
public class DrainageSpecification {

    private DrainageSpecification() {}

    public static Specification<Drainage> fromFilter(DrainageFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Episode filter — primary axis (episode detail screen)
            if (filter.getEpisodeId() != null) {
                predicates.add(cb.equal(root.get("episodeId"), filter.getEpisodeId()));
            }

            // Patient filter — secondary axis (patient timeline)
            if (filter.getPatientId() != null) {
                predicates.add(cb.equal(root.get("patientId"), filter.getPatientId()));
            }

            if (filter.getDrainageType() != null) {
                predicates.add(cb.equal(root.get("drainageType"), filter.getDrainageType()));
            }

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (Boolean.TRUE.equals(filter.getOverdue())) {
                // overdue = ACTIVE and plannedRemovalDate < today
                predicates.add(cb.equal(root.get("status"), DrainageStatus.ACTIVE));
                predicates.add(cb.lessThan(root.get("plannedRemovalDate"), LocalDate.now()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
