package com.lithoapp.analysis.dto.client;

import lombok.Data;

/**
 * Minimal projection of a Patient record returned by patient-service.
 *
 * Fields cover both the id-based validation path and the identity-search path
 * (DI / DMI / name / phone resolution). Extra fields in the patient-service
 * response are silently ignored by Jackson.
 */
@Data
public class PatientResponse {

    /** Patient primary key — matches the patientId on the analysis request. */
    private Long id;

    private String di;
    private String dmi;
    private String firstName;
    private String lastName;
    private String phone;
}
