package com.lithoapp.patientservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicationRequest {

    @NotNull(message = "hasMedication flag is required")
    private Boolean hasMedication;

    private String description;
}
