package com.lithoapp.analysis.dto.request;

import com.lithoapp.analysis.domain.enums.AnalysisType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAnalysisRequestDto {

    @NotBlank(message = "patientId is required")
    private String patientId;

    @NotBlank(message = "episodeId is required — a bilan must be linked to an episode")
    private String episodeId;

    /**
     * Identifier of the urologist (or any actor) creating this request.
     * Will be replaced by the authenticated principal once Keycloak is integrated.
     */
    @NotBlank(message = "createdBy is required")
    private String createdBy;

    @NotNull(message = "type is required (METABOLIC or STONE)")
    private AnalysisType type;
}
