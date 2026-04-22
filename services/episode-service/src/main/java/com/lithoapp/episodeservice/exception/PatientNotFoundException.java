package com.lithoapp.episodeservice.exception;

/**
 * Thrown by PatientValidationService when a referenced patient does not exist.
 * Currently unused by the stub implementation — will be thrown by FeignPatientValidationService.
 */
public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(Long patientId) {
        super("Patient not found with id: " + patientId);
    }
}
