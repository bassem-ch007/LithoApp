package com.lithoapp.analysis.dto.request;

import com.lithoapp.analysis.domain.enums.AnalysisType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateAnalysisRequestDto {

    @NotNull(message = "patientId is required")
    @Positive(message = "patientId must be a positive number")
    private Long patientId;

    @NotNull(message = "episodeId is required — an analysis must be linked to an episode")
    @Positive(message = "episodeId must be a positive number")
    private Long episodeId;

    @NotNull(message = "type is required (METABOLIC or STONE)")
    private AnalysisType type;

    // createdBy is intentionally absent — extracted from the JWT principal in the controller.
}
