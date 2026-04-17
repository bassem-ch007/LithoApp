package com.lithoapp.drainage.dto;

import com.lithoapp.drainage.enums.DrainageSide;
import com.lithoapp.drainage.enums.DrainageType;
import com.lithoapp.drainage.enums.JJType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDrainageRequest {

    @NotNull(message = "patientId is required")
    private Long patientId;

    @NotNull(message = "doctorId is required")
    private Long doctorId;

    @NotNull(message = "drainageType is required")
    private DrainageType drainageType;

    @NotNull(message = "side is required")
    private DrainageSide side;

    @NotNull(message = "placedAt is required")
    private LocalDate placedAt;

    /**
     * Optional. If not provided, it can be left null
     * (undetermined removal date) or defaulted by business logic.
     * Must be >= placedAt when provided.
     */
    private LocalDate plannedRemovalDate;

    /**
     * Required when drainageType = JJ; must be null otherwise.
     * Validation is performed in the service layer.
     */
    private JJType jjType;

    private String notes;
}
