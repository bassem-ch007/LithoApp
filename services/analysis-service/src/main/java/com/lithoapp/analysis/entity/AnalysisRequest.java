package com.lithoapp.analysis.entity;

import com.lithoapp.analysis.domain.enums.AnalysisStatus;
import com.lithoapp.analysis.domain.enums.AnalysisType;
import com.lithoapp.analysis.exception.InvalidStatusTransitionException;
import com.lithoapp.analysis.exception.RequestAlreadyCompletedException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Aggregate root of the analysis workflow.
 *
 * Owns its status-transition logic: callers invoke {@link #transitionTo(AnalysisStatus)}
 * and {@link #guardNotCompleted()} rather than manipulating the status field directly.
 *
 * External references (episodeId, patientId) are plain BIGINT IDs — no JPA
 * associations across service boundaries. Cross-service validation is delegated
 * to EpisodeValidationService and PatientValidationService at the service layer.
 */
@Entity
@Table(name = "analysis_requests")
@Getter
@Setter
public class AnalysisRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── External references ────────────────────────────────────────────────
    // Plain BIGINT IDs — no JPA associations across service boundaries.
    // Feign-based validation is handled at the service layer via
    // EpisodeValidationService and PatientValidationService.

    /** Cross-service reference to patient-service. Must match episode.patientId. */
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    /**
     * Primary case anchor — cross-service reference to episode-service.
     * Required: an analysis request cannot exist outside an episode.
     */
    @Column(name = "episode_id", nullable = false)
    private Long episodeId;

    /** The urologist (or any actor) who opened this analysis request. */
    @Column(nullable = false)
    private String createdBy;

    // ── Type & lifecycle ──────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime completedAt;

    /** Biologist (or actor) who triggered the completion. */
    @Column
    private String completedBy;

    // ── Optimistic locking ────────────────────────────────────────────────
    @Version
    private Long version;

    // ── Domain behaviour ─────────────────────────────────────────────────

    /**
     * Advance the status following the allowed state machine.
     *
     * <pre>
     *   CREATED  ──▶  IN_PROGRESS  ──▶  COMPLETED
     * </pre>
     *
     * CREATED → COMPLETED is intentionally NOT allowed: a biologist must
     * contribute at least one result (auto-transitioning to IN_PROGRESS) before
     * the bilan can be closed. This ensures completion always reflects real work.
     *
     * Throws {@link RequestAlreadyCompletedException} if already COMPLETED.
     * Throws {@link InvalidStatusTransitionException} for any other illegal move.
     */
    public void transitionTo(AnalysisStatus target) {
        if (this.status == AnalysisStatus.COMPLETED) {
            throw new RequestAlreadyCompletedException(this.id);
        }
        boolean allowed =
                (this.status == AnalysisStatus.CREATED     && target == AnalysisStatus.IN_PROGRESS)
             || (this.status == AnalysisStatus.IN_PROGRESS && target == AnalysisStatus.COMPLETED);

        if (!allowed) {
            throw new InvalidStatusTransitionException(this.status, target);
        }
        this.status = target;
    }

    /**
     * Guard used by result-modification operations.
     * Throws if the request is already COMPLETED (immutable).
     */
    public void guardNotCompleted() {
        if (this.status == AnalysisStatus.COMPLETED) {
            throw new RequestAlreadyCompletedException(this.id);
        }
    }

    // ── Factory ───────────────────────────────────────────────────────────

    public static AnalysisRequest create(Long patientId, Long episodeId,
                                         String createdBy, AnalysisType type) {
        AnalysisRequest r = new AnalysisRequest();
        r.patientId  = patientId;
        r.episodeId  = episodeId;
        r.createdBy  = createdBy;
        r.type       = type;
        r.status     = AnalysisStatus.CREATED;
        r.createdAt  = LocalDateTime.now();
        return r;
    }
}
