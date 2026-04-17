package com.lithoapp.patientservice.dto.response;

import com.lithoapp.patientservice.enums.Gender;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PatientResponse {

    private Long id;

    // ── Identity ──────────────────────────────────────────────────────────────
    private String di;
    private String dmi;

    // ── Demographics ──────────────────────────────────────────────────────────
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private Double height;
    private Double weight;
    private String address;
    private String email;
    private String phone;
    private Boolean active;

    // ── Audit ──────────────────────────────────────────────────────────────────
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ── Clinical sections ──────────────────────────────────────────────────────
    private ClinicalInfoResponse clinicalInfo;
    private MedicationResponse medication;
    private List<String> associatedDiseases;
    private List<String> geneticDiseases;
    private List<String> anatomicalAnomalies;
}
