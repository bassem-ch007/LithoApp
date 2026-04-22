package com.lithoapp.episodeservice.exception;

/**
 * Thrown when an episode creation is attempted for a patient who exists in
 * patient-service but is marked inactive.
 *
 * Maps to HTTP 422 Unprocessable Entity — the request is syntactically valid
 * but cannot be processed because of the patient's current state.
 */
public class PatientInactiveException extends RuntimeException {

    public PatientInactiveException(Long patientId) {
        super("Patient " + patientId + " is inactive. " +
              "Episodes can only be opened for active patients.");
    }
}
