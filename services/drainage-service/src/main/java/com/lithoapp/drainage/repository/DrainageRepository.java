package com.lithoapp.drainage.repository;

import com.lithoapp.drainage.entity.Drainage;
import com.lithoapp.drainage.enums.DrainageSide;
import com.lithoapp.drainage.enums.DrainageStatus;
import com.lithoapp.drainage.enums.DrainageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DrainageRepository extends JpaRepository<Drainage, UUID>,
        JpaSpecificationExecutor<Drainage> {

    // ── Episode-scoped queries ────────────────────────────────────────────────

    /**
     * All drainages for a given episode.
     * Used by the episode detail screen to load the drainage panel.
     */
    List<Drainage> findByEpisodeId(Long episodeId);

    /**
     * Duplicate active drainage guard — scoped to episode.
     * Prevents placing two ACTIVE drainages of the same type and side within
     * the same stone case. Does NOT block the same combination across different
     * episodes of the same patient (which is clinically valid).
     */
    boolean existsByEpisodeIdAndDrainageTypeAndSideAndStatus(
            Long episodeId,
            DrainageType drainageType,
            DrainageSide side,
            DrainageStatus status
    );

    // ── Patient-scoped queries ────────────────────────────────────────────────

    /**
     * All drainages for a patient across all their episodes.
     * Used for patient-level timeline / reporting views.
     */
    List<Drainage> findByPatientId(Long patientId);

    // ── Scheduler queries ─────────────────────────────────────────────────────

    /**
     * Finds active drainages whose planned removal date equals a specific date
     * and whose same-day reminder has not been sent yet.
     */
    List<Drainage> findByStatusAndPlannedRemovalDateAndDayOfReminderSentAtIsNull(
            DrainageStatus status,
            LocalDate plannedRemovalDate
    );

    /**
     * Finds active drainages whose planned removal date equals a specific date
     * and whose pre-reminder has not been sent yet.
     */
    List<Drainage> findByStatusAndPlannedRemovalDateAndPreReminderSentAtIsNull(
            DrainageStatus status,
            LocalDate plannedRemovalDate
    );
}
