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
 * External references (patientId, episodeId, createdBy) are plain strings.
 * No JPA associations to other services — Feign integration comes later.
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
    // Plain IDs — Feign/patient-service integration added in a later phase.
    @Column(nullable = false)
    private String patientId;

    @Column(nullable = false)
    private String episodeId;

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

    public static AnalysisRequest create(String patientId, String episodeId,
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
