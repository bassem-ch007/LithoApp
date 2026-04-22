package com.lithoapp.drainage.dto;

import com.lithoapp.drainage.enums.DrainageSide;
import com.lithoapp.drainage.enums.DrainageType;
import com.lithoapp.drainage.enums.JJType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDrainageRequest {

    /**
     * The episode this drainage belongs to.
     * Required — reflects the workflow: patient → episode → drainage.
     * Cross-service reference to episode-service.
     */
    @NotNull(message = "episodeId is required")
    private Long episodeId;

    /**
     * Patient who owns the episode.
     * Must match the patient of the referenced episode.
     * Kept for filtering, scheduler logs, and reporting.
     */
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
     * Optional. When not provided, a default is computed by DrainageDurationPolicy
     * based on the drainage type and jjType.
     * Must be >= placedAt when explicitly provided.
     */
    private LocalDate plannedRemovalDate;

    /**
     * Required when drainageType = JJ; must be null otherwise.
     * Validation is performed in the service layer.
     */
    private JJType jjType;

    private String notes;
}
