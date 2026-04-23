package com.lithoapp.patientservice.dto.response;

import com.lithoapp.patientservice.enums.Gender;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Lightweight projection used in search / list results.
 */
@Data
@Builder
public class PatientSummaryResponse {

    private Long id;
    private String di;
    private String dmi;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String phone;
}
