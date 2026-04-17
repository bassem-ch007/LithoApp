package com.lithoapp.patientservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicationResponse {

    private Long id;
    private Boolean hasMedication;
    private String description;
}
