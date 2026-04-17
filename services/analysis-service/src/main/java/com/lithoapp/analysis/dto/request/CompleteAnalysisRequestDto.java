package com.lithoapp.analysis.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompleteAnalysisRequestDto {

    /**
     * Biologist (or actor) explicitly marking the request as COMPLETED.
     * Will be replaced by the authenticated principal once Keycloak is integrated.
     */
    @NotBlank(message = "completedBy is required")
    private String completedBy;
}
