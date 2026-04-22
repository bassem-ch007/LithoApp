package com.lithoapp.drainage.entity;

import com.lithoapp.drainage.enums.DrainageSide;
import com.lithoapp.drainage.enums.DrainageStatus;
import com.lithoapp.drainage.enums.DrainageType;
import com.lithoapp.drainage.enums.JJType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "drainages",
        indexes = {
                // Primary query: all drainages for an episode (episode detail screen)
                @Index(name = "idx_drainage_episode_id",      columnList = "episode_id"),
                // Secondary query: all drainages for a patient (patient timeline)
                @Index(name = "idx_drainage_patient_id",      columnList = "patient_id"),
                // Scheduler queries
                @Index(name = "idx_drainage_status",          columnList = "status"),
                @Index(name = "idx_drainage_planned_removal", columnList = "planned_removal_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Drainage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ── Cross-service references ─────────────────────────────────────────────
    // Plain IDs — no JPA relations across service boundaries.
    // episodeId is validated via EpisodeValidationService (stub now, Feign later).

    /**
     * The clinical episode this drainage belongs to.
     * This is the primary case anchor — reflects the workflow:
     *   patient → episode → drainage
     *
     * Cross-service reference to episode-service.
     * The duplicate active drainage guard operates at episode scope:
     *   no two ACTIVE drainages of same type+side within the same episode.
     */
    @Column(name = "episode_id", nullable = false)
    private Long episodeId;

    /**
     * Patient reference — kept alongside episodeId for filtering,
     * scheduler log context, and reporting.
     * Must match the patient of the referenced episode
     * (enforced by EpisodeValidationService when Feign is wired).
     */
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    /**
     * Treating doctor reference.
     * Cross-service reference — no FK constraint.
     */
    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    // ── Clinical fields ──────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "drainage_type", nullable = false, length = 20)
    private DrainageType drainageType;

    @Enumerated(EnumType.STRING)
    @Column(name = "side", nullable = false, length = 10)
    private DrainageSide side;

    @Column(name = "placed_at", nullable = false)
    private LocalDate placedAt;

    /** Expected removal date. May be updated after insertion. */
    @Column(name = "planned_removal_date")
    private LocalDate plannedRemovalDate;

    /** Set when the drainage device is physically removed. */
    @Column(name = "removed_at")
    private LocalDate removedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private DrainageStatus status;

    /**
     * Sub-type of JJ stent. Must be non-null when drainageType = JJ,
     * and must be null for all other types.
     * Validated in the service layer.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "jj_type", length = 20)
    private JJType jjType;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ── Notification tracking ────────────────────────────────────────────────
    // Reserved for future Notification Service integration.
    // The scheduler populates these; the Notification Service will consume them.

    @Column(name = "pre_reminder_sent_at")
    private LocalDateTime preReminderSentAt;

    @Column(name = "day_of_reminder_sent_at")
    private LocalDateTime dayOfReminderSentAt;

    // ── Audit ────────────────────────────────────────────────────────────────

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
