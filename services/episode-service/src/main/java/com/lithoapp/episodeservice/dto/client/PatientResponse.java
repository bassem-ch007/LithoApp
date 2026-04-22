package com.lithoapp.episodeservice.dto.client;

import lombok.Data;

/**
 * Minimal projection of a Patient record returned by patient-service.
 *
 * Only the fields required for episode-level validation are mapped here.
 * Any extra fields in the patient-service response are silently ignored by Jackson.
 */
@Data
public class PatientResponse {

    /** Patient primary key — matches the patientId supplied on episode creation. */
    private Long id;

    /**
     * Whether this patient is currently active.
     * Episodes may only be opened for active patients.
     */
    private boolean active;
}
