package com.lithoapp.analysis.client;

import com.lithoapp.analysis.dto.client.PatientResponse;

import java.util.Optional;

/**
 * Abstraction over the remote patient-service HTTP API.
 *
 * <p>Returns {@link Optional} so callers decide how to handle a missing patient
 * without catching exceptions at the call site.
 *
 * <p>Current implementation: {@link FeignPatientServiceClient} — thin adapter over
 * {@link PatientFeignClient} that translates {@code feign.FeignException.NotFound}
 * into {@code Optional.empty()} and other Feign errors into
 * {@link com.lithoapp.analysis.exception.PatientServiceUnavailableException}.
 */
public interface PatientServiceClient {

    /**
     * Fetches a patient record by its primary key.
     *
     * @param patientId the patient to look up
     * @return the patient data, or {@code Optional.empty()} on HTTP 404
     * @throws com.lithoapp.analysis.exception.PatientServiceUnavailableException
     *         if patient-service cannot be reached or returns an unexpected error
     */
    Optional<PatientResponse> findById(Long patientId);
}
