package com.lithoapp.analysis.client;

import com.lithoapp.analysis.dto.client.PatientPageResponse;
import com.lithoapp.analysis.dto.client.PatientResponse;
import com.lithoapp.analysis.exception.PatientServiceUnavailableException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Feign-backed implementation of {@link PatientServiceClient}.
 *
 * <p>Delegates to {@link PatientFeignClient} and preserves the legacy semantics:
 * <ul>
 *   <li>HTTP 200 → returns the deserialized {@link PatientResponse}</li>
 *   <li>HTTP 404 → returns {@code Optional.empty()}</li>
 *   <li>Any other Feign error (network / 5xx / unexpected 4xx) →
 *       throws {@link PatientServiceUnavailableException} (mapped to HTTP 503).</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FeignPatientServiceClient implements PatientServiceClient {

    /**
     * Upper bound passed to patient-service to flatten its paginated search
     * into a single response. The search is always driven by highly-selective
     * fields (DI / DMI / name / phone) so the matching set is expected to be
     * small; a generous page size avoids the need for cross-service pagination.
     */
    private static final int SEARCH_PAGE_SIZE = 500;

    private final PatientFeignClient patientFeignClient;

    @Override
    public Optional<PatientResponse> findById(Long patientId) {
        log.debug("Fetching patient from patient-service via Feign: patientId={}", patientId);

        try {
            return Optional.ofNullable(patientFeignClient.getById(patientId));

        } catch (FeignException.NotFound e) {
            log.debug("patient-service returned 404 for patientId={}", patientId);
            return Optional.empty();

        } catch (FeignException e) {
            log.error("patient-service call failed for patientId={} (status {}): {}",
                    patientId, e.status(), e.getMessage());
            throw new PatientServiceUnavailableException(
                    "Patient service is currently unavailable. Please try again later.");
        }
    }

    @Override
    public List<PatientResponse> search(String di, String dmi, String name, String phone) {
        log.debug("Searching patients via Feign: di={}, dmi={}, name={}, phone={}",
                di, dmi, name, phone);

        try {
            PatientPageResponse page = patientFeignClient.search(di, dmi, name, phone, SEARCH_PAGE_SIZE);
            if (page == null || page.getContent() == null) {
                return Collections.emptyList();
            }
            return page.getContent();

        } catch (FeignException e) {
            log.error("patient-service search failed (status {}): {}", e.status(), e.getMessage());
            throw new PatientServiceUnavailableException(
                    "Patient service is currently unavailable. Please try again later.");
        }
    }
}
