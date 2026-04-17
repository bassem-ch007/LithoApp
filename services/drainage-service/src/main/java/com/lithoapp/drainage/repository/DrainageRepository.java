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

    /**
     * Used to enforce the "no duplicate ACTIVE drainage per patient/type/side" rule.
     */
    boolean existsByPatientIdAndDrainageTypeAndSideAndStatus(
            Long patientId,
            DrainageType drainageType,
            DrainageSide side,
            DrainageStatus status
    );

    /**
     * Finds active drainages whose planned removal date equals a specific date
     * and whose day-of reminder has not been sent yet.
     * Used by the scheduler for same-day reminders.
     */
    List<Drainage> findByStatusAndPlannedRemovalDateAndDayOfReminderSentAtIsNull(
            DrainageStatus status,
            LocalDate plannedRemovalDate
    );

    /**
     * Finds active drainages whose planned removal date equals a specific date
     * and whose pre-reminder has not been sent yet.
     * Used by the scheduler for X-day-before reminders.
     */
    List<Drainage> findByStatusAndPlannedRemovalDateAndPreReminderSentAtIsNull(
            DrainageStatus status,
            LocalDate plannedRemovalDate
    );
}
