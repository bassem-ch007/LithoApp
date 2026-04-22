package com.lithoapp.drainage.client;

import com.lithoapp.drainage.dto.client.PatientResponse;

import java.util.Optional;

/**
 * Abstraction over the remote patient-service HTTP API.
 *
 * <p>Returns {@link Optional} so callers decide how to handle a missing
 * patient without catching exceptions at the call site.
 *
 * <p>Current implementation: {@link HttpPatientServiceClient} — direct
 * RestTemplate call to {@code patient-service.base-url}/patients/{id}.
 *
 * <p>Future: swap for a Feign-annotated interface once Eureka is enabled.
 * No other changes needed — the interface contract is identical.
 */
public interface PatientServiceClient {

    /**
     * Fetches a patient record by its primary key.
     *
     * @param patientId the patient to look up
     * @return the patient data, or {@code Optional.empty()} on HTTP 404
     * @throws com.lithoapp.drainage.exception.PatientServiceUnavailableException
     *         if patient-service cannot be reached or returns an unexpected error
     */
    Optional<PatientResponse> findById(Long patientId);
}
