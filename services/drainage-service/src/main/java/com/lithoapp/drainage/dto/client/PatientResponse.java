package com.lithoapp.drainage.dto.client;

import lombok.Data;

/**
 * Minimal projection of a Patient record returned by patient-service.
 *
 * Only the fields required for drainage-level validation are mapped here.
 * Extra fields in the patient-service response are silently ignored by Jackson.
 */
@Data
public class PatientResponse {

    /** Patient primary key — matches the patientId on the drainage request. */
    private Long id;
}
