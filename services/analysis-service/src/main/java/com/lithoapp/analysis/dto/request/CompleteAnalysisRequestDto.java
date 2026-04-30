package com.lithoapp.analysis.dto.request;

import lombok.Data;

/**
 * Request body for the complete-analysis-request endpoint.
 *
 * completedBy is intentionally absent — the actor is extracted from the JWT
 * principal in the controller and passed explicitly to the service.
 * This DTO is kept for future extensibility (e.g. completion notes).
 */
@Data
public class CompleteAnalysisRequestDto {
    // No fields required from client at this time.
}
