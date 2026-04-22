package com.lithoapp.analysis.dto.response;

import com.lithoapp.analysis.domain.enums.AnalysisStatus;
import com.lithoapp.analysis.domain.enums.AnalysisType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnalysisRequestDto {

    private Long id;

    /** Primary case anchor — the episode this analysis belongs to. */
    private Long episodeId;

    /** Patient reference — must match episode.patientId. */
    private Long patientId;
    private String createdBy;
    private AnalysisType type;
    private AnalysisStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String completedBy;
    private Long version;

    /**
     * Present when type == METABOLIC. Shows which PDFs have been uploaded
     * and how many of 3 are complete. Null for STONE requests.
     */
    private MetabolicResultDto metabolicResult;

    /**
     * Present when type == STONE. Shows the current structured result state.
     * Null for METABOLIC requests.
     */
    private StoneResultDto stoneResult;
}
