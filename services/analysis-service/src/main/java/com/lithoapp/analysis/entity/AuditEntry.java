package com.lithoapp.analysis.entity;

import com.lithoapp.analysis.domain.enums.AuditActionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Immutable audit record for a single event within an analysis request's lifecycle.
 *
 * Design notes:
 * - Not linked via a JPA @ManyToOne to AnalysisRequest (plain FK column only).
 *   This ensures audit records survive even if the parent is ever deleted,
 *   and avoids cascade surprises.
 * - Written in the same transaction as the operation it describes: a rolled-back
 *   operation never leaves a dangling audit entry.
 * - {@code targetField} carries the specific field name or document type changed.
 * - {@code oldValue} / {@code newValue} are TEXT to accommodate storage keys,
 *   enum names, and free-form field values without length constraints.
 */
@Entity
@Table(name = "audit_entries",
       indexes = @Index(name = "idx_audit_request_id", columnList = "analysis_request_id"))
@Getter
@Setter
public class AuditEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** FK to the affected AnalysisRequest (plain column, not a JPA association). */
    @Column(name = "analysis_request_id", nullable = false)
    private Long analysisRequestId;

    /** The user who triggered this action (biologistId, doctorId, etc.). */
    @Column(nullable = false)
    private String actorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditActionType actionType;

    /**
     * The specific field or resource changed.
     * Examples: "status", "BLOOD_TEST", "morphSize", "finalStoneType".
     * Null for coarse-grained actions like REQUEST_CREATED.
     */
    @Column
    private String targetField;

    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Column(columnDefinition = "TEXT")
    private String newValue;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
