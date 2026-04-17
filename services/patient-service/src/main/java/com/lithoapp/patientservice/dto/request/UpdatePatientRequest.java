package com.lithoapp.patientservice.dto.request;

import com.lithoapp.patientservice.enums.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * All fields are optional for partial updates.
 * Only non-null fields will be applied.
 */
@Data
@Builder
public class UpdatePatientRequest {

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    private Gender gender;

    @Positive(message = "Height must be a positive value")
    private Double height;

    @Positive(message = "Weight must be a positive value")
    private Double weight;

    private String address;

    @Email(message = "Email must be a valid address")
    private String email;

    private String phone;

    private Boolean active;

    @Valid
    private ClinicalInfoRequest clinicalInfo;

    @Valid
    private MedicationRequest medication;

    private List<String> associatedDiseases;

    private List<String> geneticDiseases;

    private List<String> anatomicalAnomalies;
}
