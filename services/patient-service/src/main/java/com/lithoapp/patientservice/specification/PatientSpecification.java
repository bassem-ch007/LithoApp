package com.lithoapp.patientservice.specification;

import com.lithoapp.patientservice.entity.Patient;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class PatientSpecification {

    private PatientSpecification() {}

    /**
     * Builds a dynamic {@link Specification} for patient search.
     * Only non-blank parameters produce a predicate — null or empty values are ignored entirely,
     * which avoids the PostgreSQL {@code lower(bytea)} type-inference error that occurs
     * when a nullable JPQL parameter appears in {@code :param IS NULL} expressions.
     */
    public static Specification<Patient> search(String di, String dmi, String name, String phone) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hasText(di)) {
                predicates.add(cb.like(
                        cb.lower(root.get("di")),
                        contains(di.toLowerCase())
                ));
            }

            if (hasText(dmi)) {
                predicates.add(cb.like(
                        cb.lower(root.get("dmi")),
                        contains(dmi.toLowerCase())
                ));
            }

            if (hasText(name)) {
                // Concatenate firstName + ' ' + lastName, then LOWER + LIKE
                Expression<String> fullName = cb.concat(
                        cb.concat(root.get("firstName"), cb.literal(" ")),
                        root.get("lastName")
                );
                predicates.add(cb.like(
                        cb.lower(fullName),
                        contains(name.toLowerCase())
                ));
            }

            if (hasText(phone)) {
                Expression<String> normalizedPhone = cb.function(
                        "replace",
                        String.class,
                        root.get("phone"),
                        cb.literal(" "),
                        cb.literal("")
                );

                predicates.add(cb.equal(
                        normalizedPhone,
                        phone.replace(" ", "")
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String contains(String value) {
        return "%" + value + "%";
    }
}
