package com.lithoapp.patientservice.dto.request;

import com.lithoapp.patientservice.enums.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class CreatePatientRequest {

    // ── Identity ──────────────────────────────────────────────────────────────

    @NotBlank(message = "DI is required")
    private String di;

    @NotBlank(message = "DMI is required")
    private String dmi;

    // ── Demographics ──────────────────────────────────────────────────────────

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @Positive(message = "Height must be a positive value")
    private Double height;

    @Positive(message = "Weight must be a positive value")
    private Double weight;

    private String address;

    @Email(message = "Email must be a valid address")
    private String email;

    private String phone;

    // ── Clinical sections ─────────────────────────────────────────────────────

    @Valid
    private ClinicalInfoRequest clinicalInfo;

    @Valid
    private MedicationRequest medication;

    private List<String> associatedDiseases;

    private List<String> geneticDiseases;

    private List<String> anatomicalAnomalies;
}
