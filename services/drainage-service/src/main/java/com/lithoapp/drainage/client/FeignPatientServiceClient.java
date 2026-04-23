package com.lithoapp.drainage.client;

import com.lithoapp.drainage.dto.client.PatientResponse;
import com.lithoapp.drainage.exception.PatientServiceUnavailableException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
}
