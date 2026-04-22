package com.lithoapp.drainage.dto.client;

import lombok.Data;

/**
 * Minimal projection of an Episode record returned by episode-service.
 *
 * Only the fields required for drainage-level validation are mapped here.
 * Extra fields in the episode-service response (title, notes, openedAt, etc.)
 * are silently ignored by Jackson.
 */
@Data
public class EpisodeResponse {

    /** Episode primary key — used to confirm the episode was found. */
    private Long id;

    /**
     * Patient this episode belongs to.
     * Must match the patientId supplied in the drainage creation request,
     * otherwise the patient → episode → drainage chain is corrupt.
     */
    private Long patientId;
}
