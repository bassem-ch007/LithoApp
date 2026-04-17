package com.lithoapp.analysis.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Partial-update DTO for stone result fields.
 *
 * Semantics: a null field means "do not change this field".
 * Only non-null fields are applied and audited.
 * This allows biologists to fill the result progressively
 * without having to resend the entire document each time.
 *
 * To explicitly clear a field, send an empty string "".
 */
@Data
public class UpdateStoneResultDto {

    /**
     * Biologist performing this update.
     * Will be replaced by the authenticated principal once Keycloak is integrated.
     */
    @NotBlank(message = "modifiedBy is required")
    private String modifiedBy;

    // ── Morphological fields (all optional / partial) ─────────────────────
    private String morphSize;
    private String morphSurface;
    private String morphColor;
    private String morphSection;
    private String morphOuterLayers;
    private String morphCore;

    // ── Spectrophotometry fields (all optional / partial) ─────────────────
    private String spectroSurface;
    private String spectroSection;
    private String spectroOuterLayers;
    private String spectroCore;

    // ── Final classification (optional / partial) ─────────────────────────
    /** Required to be non-blank before the request can be COMPLETED. */
    private String finalStoneType;
}
