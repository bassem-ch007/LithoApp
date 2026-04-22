package com.lithoapp.analysis.exception;

/**
 * Thrown when a patientId supplied on analysis request creation does not
 * correspond to any patient in patient-service.
 *
 * Maps to HTTP 404 Not Found.
 */
public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(Long patientId) {
        super("Patient not found with id: " + patientId);
    }
}
