package com.lithoapp.episodeservice.client;

import com.lithoapp.episodeservice.dto.client.PatientResponse;

import java.util.Optional;

/**
 * Abstraction over the remote patient-service HTTP API.
 *
 * <p>Callers receive an {@link Optional} so they can decide how to handle
 * a missing patient without catching exceptions at the call site.
 *
 * <p>Current implementation: {@link HttpPatientServiceClient} — direct
 * RestTemplate call to {@code patient-service.base-url}/api/patients/{id}.
 *
 * <p>Future: swap for a Feign-annotated interface once service discovery
 * (Eureka) is enabled. No other changes needed — the interface contract
 * is identical.
 */
public interface PatientServiceClient {

    /**
     * Fetches a patient record by its primary key.
     *
     * @param patientId the patient to look up
     * @return the patient data, or {@code Optional.empty()} if the patient
     *         does not exist in patient-service (HTTP 404)
     * @throws com.lithoapp.episodeservice.exception.PatientServiceUnavailableException
     *         if patient-service cannot be reached or returns an unexpected error
     */
    Optional<PatientResponse> findById(Long patientId);
}
