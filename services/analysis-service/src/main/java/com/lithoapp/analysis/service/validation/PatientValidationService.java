package com.lithoapp.analysis.service.validation;

/**
 * Cross-service patient existence validation.
 *
 * Active implementation: {@link PatientValidationServiceImpl} (@Primary), backed by
 * {@link com.lithoapp.analysis.client.FeignPatientServiceClient} (OpenFeign).
 * Fallback: {@link StubPatientValidationService} (no-op, for local runs without patient-service).
 */
public interface PatientValidationService {

    /**
     * Verifies that a patient with the given ID exists in patient-service.
     *
     * @param patientId the patient identifier to validate
     * @throws com.lithoapp.analysis.exception.PatientNotFoundException if the patient does not exist
     */
    void validatePatientExists(Long patientId);
}
