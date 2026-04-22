package com.lithoapp.drainage.exception;

/**
 * Thrown when patient-service cannot be reached or returns an unexpected
 * error during patient validation.
 *
 * Maps to HTTP 503 Service Unavailable — the caller should retry later.
 */
public class PatientServiceUnavailableException extends RuntimeException {

    public PatientServiceUnavailableException(String message) {
        super(message);
    }
}
