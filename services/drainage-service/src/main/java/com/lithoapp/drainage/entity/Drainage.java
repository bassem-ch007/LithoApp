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
    uniqueConstraints = {
        // Enforced at DB level: one ACTIVE drainage per (patient, type, side)
        // The partial uniqueness on status=ACTIVE is handled in business logic
        // because standard SQL unique constraints cannot filter by column value portably.
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

    // ── External references ──────────────────────────────────────────────────
    // These are plain IDs for now. When Feign clients are introduced, real
    // validation against Patient/Doctor/Episode services can be added here.

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long doctorId;

    // ── Clinical fields ──────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DrainageType drainageType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DrainageSide side;

    @Column(nullable = false)
    private LocalDate placedAt;

    /** Expected removal date. May be updated after insertion. */
    private LocalDate plannedRemovalDate;

    /** Set when the drainage is physically removed. */
    private LocalDate removedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DrainageStatus status;

    /**
     * Sub-type of JJ stent. Must be non-null when drainageType = JJ,
     * and must be null for all other types.
     */
    @Enumerated(EnumType.STRING)
    private JJType jjType;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // ── Notification tracking ────────────────────────────────────────────────
    // These fields are reserved for the future Notification Service.
    // The scheduler populates them; the Notification Service will consume them.

    /** Timestamp when the X-day pre-reminder was (logically) sent. */
    private LocalDateTime preReminderSentAt;

    /** Timestamp when the same-day reminder was (logically) sent. */
    private LocalDateTime dayOfReminderSentAt;

    // ── Audit fields ─────────────────────────────────────────────────────────

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
