package com.lithoapp.analysis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Structured result for a STONE analysis request.
 * Created (empty) when the parent AnalysisRequest is created.
 * Fields are filled progressively by one or more biologists.
 *
 * @Version provides optimistic locking: if two biologists submit concurrent
 * updates, the second write will get a 409 Conflict response and must retry.
 *
 * Completion requirement: {@code finalStoneType} must be non-blank.
 */
@Entity
@Table(name = "stone_results")
@Getter
@Setter
public class StoneResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long analysisRequestId;

    // ── Morphological analysis ────────────────────────────────────────────
    private String morphSize;
    private String morphSurface;
    private String morphColor;
    private String morphSection;
    private String morphOuterLayers;
    private String morphCore;

    // ── Infrared spectrophotometry ────────────────────────────────────────
    private String spectroSurface;
    private String spectroSection;
    private String spectroOuterLayers;
    private String spectroCore;

    // ── Final classification ──────────────────────────────────────────────
    /** Required before the request can be marked COMPLETED. */
    private String finalStoneType;

    // ── Provenance ────────────────────────────────────────────────────────
    private String lastModifiedBy;
    private LocalDateTime lastModifiedAt;

    // ── Optimistic locking ────────────────────────────────────────────────
    @Version
    private Long version;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // ── Factory ───────────────────────────────────────────────────────────

    public static StoneResult forRequest(Long analysisRequestId) {
        StoneResult sr = new StoneResult();
        sr.analysisRequestId = analysisRequestId;
        sr.createdAt = LocalDateTime.now();
        return sr;
    }
}
