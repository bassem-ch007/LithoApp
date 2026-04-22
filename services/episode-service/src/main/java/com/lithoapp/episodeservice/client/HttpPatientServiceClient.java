package com.lithoapp.episodeservice.client;

import com.lithoapp.episodeservice.dto.client.PatientResponse;
import com.lithoapp.episodeservice.exception.PatientServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * RestTemplate-based implementation of {@link PatientServiceClient}.
 *
 * <p>Calls {@code GET {patient-service.base-url}/api/patients/{id}}.
 * Returns {@code Optional.empty()} on 404; throws
 * {@link PatientServiceUnavailableException} on network errors or unexpected
 * HTTP status codes, which the global handler maps to HTTP 503.
 *
 * <p>When Feign + Eureka are enabled, replace this class with a
 * {@code @FeignClient}-annotated interface and annotate it {@code @Primary}.
 * Delete this class (or keep it behind {@code @Profile("no-feign")}).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpPatientServiceClient implements PatientServiceClient {

    private final RestTemplate restTemplate;

    @Value("${patient-service.base-url}")
    private String baseUrl;

    @Override
    public Optional<PatientResponse> findById(Long patientId) {
        String url = baseUrl + "/patients/" + patientId;
        log.debug("Fetching patient from patient-service: GET {}", url);

        try {
            ResponseEntity<PatientResponse> response =
                    restTemplate.getForEntity(url, PatientResponse.class);
            return Optional.ofNullable(response.getBody());

        } catch (HttpClientErrorException.NotFound e) {
            log.debug("patient-service returned 404 for patientId={}", patientId);
            return Optional.empty();

        } catch (HttpClientErrorException e) {
            log.error("patient-service returned {} for patientId={}: {}",
                    e.getStatusCode(), patientId, e.getMessage());
            throw new PatientServiceUnavailableException(
                    "Unexpected response from patient-service (status " + e.getStatusCode() + "). " +
                    "Please try again later.");

        } catch (RestClientException e) {
            log.error("Could not reach patient-service for patientId={}: {}", patientId, e.getMessage());
            throw new PatientServiceUnavailableException(
                    "Patient service is currently unavailable. Please try again later.");
        }
    }
}
