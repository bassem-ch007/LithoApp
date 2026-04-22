package com.lithoapp.analysis.exception;

/**
 * Thrown when an analysis request is created for a patient who exists in
 * patient-service but is currently inactive.
 *
 * Maps to HTTP 422 Unprocessable Entity — the request body is valid but the
 * patient's current state prevents the operation.
 */
public class PatientInactiveException extends RuntimeException {

    public PatientInactiveException(Long patientId) {
        super("Patient " + patientId + " is inactive. " +
              "Analysis requests can only be created for active patients.");
    }
}
