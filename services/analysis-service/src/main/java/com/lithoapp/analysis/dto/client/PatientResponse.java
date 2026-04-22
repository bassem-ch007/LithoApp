package com.lithoapp.analysis.dto.client;

import lombok.Data;

/**
 * Minimal projection of a Patient record returned by patient-service.
 *
 * Only the fields required for analysis-level validation are mapped here.
 * Extra fields in the patient-service response are silently ignored by Jackson.
 */
@Data
public class PatientResponse {

    /** Patient primary key — matches the patientId on the analysis request. */
    private Long id;

    /**
     * Whether this patient is currently active.
     * Analysis requests may only be created for active patients.
     */
    private boolean active;
}
