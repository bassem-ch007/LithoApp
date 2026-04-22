package com.lithoapp.analysis.dto.request;

import com.lithoapp.analysis.domain.enums.AnalysisType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateAnalysisRequestDto {

    /**
     * The patient this analysis belongs to.
     * Must match the patient of the referenced episode.
     * Cross-service reference to patient-service — validated at creation time
     * by PatientValidationService (stub → future Feign impl).
     */
    @NotNull(message = "patientId is required")
    @Positive(message = "patientId must be a positive number")
    private Long patientId;

    /**
     * The episode this analysis belongs to.
     * Required — reflects the workflow: patient → episode → analysis.
     * Must reference an episode that belongs to the same patient.
     * Cross-service reference to episode-service — validated at creation time
     * by EpisodeValidationService (stub → future Feign impl).
     */
    @NotNull(message = "episodeId is required — an analysis must be linked to an episode")
    @Positive(message = "episodeId must be a positive number")
    private Long episodeId;

    /**
     * Identifier of the urologist (or any actor) creating this request.
     * Will be replaced by the authenticated principal once Keycloak is integrated.
     */
    @NotBlank(message = "createdBy is required")
    private String createdBy;

    @NotNull(message = "type is required (METABOLIC or STONE)")
    private AnalysisType type;
}
