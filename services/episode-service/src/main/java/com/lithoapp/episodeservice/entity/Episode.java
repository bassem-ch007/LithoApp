package com.lithoapp.episodeservice.entity;

import com.lithoapp.episodeservice.enums.EpisodeStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A clinical episode represents one stone event (case) for a patient.
 *
 * Episode is a lightweight case container — its sole structural purpose is to
 * anchor related clinical activity to one patient and one stone event:
 *
 *   patient-service  ──► patientId (cross-service reference)
 *   analysis-service ──► stores episodeId on each AnalysisRequest
 *   drainage-service ──► stores episodeId on each Drainage record
 *
 * Episode does NOT duplicate the workflows of those services.
 * Stone anatomy, analysis results, treatment stages, and drainage lifecycle
 * are all owned by their respective services.
 */
@Entity
@Table(
        name = "episodes",
        indexes = {
                @Index(name = "idx_episode_patient_id",        columnList = "patient_id"),
                @Index(name = "idx_episode_patient_status",    columnList = "patient_id, status"),
                @Index(name = "idx_episode_opened_at",         columnList = "opened_at DESC")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Cross-service reference to the patient in patient-service.
     * Intentionally a plain Long — no JPA @ManyToOne across service boundaries.
     * Patient existence is validated via PatientValidationService (stub now, Feign later).
     */
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    /**
     * Organizational status of this case folder.
     * ACTIVE  = stone event ongoing.
     * CLOSED  = case resolved (set by the urologist when the event is considered over).
     * Not a treatment workflow state — treatment details live in other services.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private EpisodeStatus status;

    /**
     * Date the case was opened — when the stone event was first identified
     * or when the patient first presented with this stone event.
     */
    @Column(name = "opened_at", nullable = false)
    private LocalDate openedAt;

    /**
     * Optional short label for this case folder.
     * Helps the urologist identify the episode in a list without reading notes.
     * Example: "2024 Left Ureteral Stone", "Recurrence — Right Kidney"
     */
    @Column(name = "title", length = 255)
    private String title;

    /**
     * Light free-text case summary.
     * Intended as an overarching case narrative, NOT analysis results or treatment notes.
     * Analysis findings belong in analysis-service. Drainage notes belong in drainage-service.
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * True if this episode is a stone recurrence for this patient.
     * Set at case creation by the urologist.
     * Purely organizational — used to filter/flag returning stone-formers.
     */
    @Column(name = "is_recurrence", nullable = false)
    private boolean recurrence;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
