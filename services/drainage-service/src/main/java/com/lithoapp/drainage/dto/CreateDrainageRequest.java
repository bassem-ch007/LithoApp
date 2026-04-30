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

    /**
     * doctorId is intentionally removed from client input.
     *
     * TRANSITIONAL NOTE: The Drainage entity stores doctorId as Long, but the
     * Keycloak JWT subject is a UUID string. These types are incompatible.
     * For now, doctorId is set to null on new drainages (column made nullable).
     * The treating doctor's identity is logged from the JWT preferred_username
     * in DrainageServiceImpl but not yet persisted in a typed column.
     *
     * Future work: add a String doctorUsername column to Drainage, or change
     * the doctorId column to VARCHAR and store the Keycloak subject UUID.
     */

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
