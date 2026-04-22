package com.lithoapp.drainage.dto;

import com.lithoapp.drainage.enums.DrainageSide;
import com.lithoapp.drainage.enums.DrainageStatus;
import com.lithoapp.drainage.enums.DrainageType;
import com.lithoapp.drainage.enums.JJType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DrainageResponse {

    private UUID id;

    /** Primary case anchor — the episode this drainage belongs to. */
    private Long episodeId;

    private Long patientId;
    private Long doctorId;

    private DrainageType drainageType;
    private DrainageSide side;

    private LocalDate placedAt;
    private LocalDate plannedRemovalDate;
    private LocalDate removedAt;

    private DrainageStatus status;

    private JJType jjType;

    private String notes;

    /**
     * Computed field: true when status=ACTIVE and plannedRemovalDate < today.
     * Not persisted — calculated at mapping time.
     */
    private boolean overdue;

    // Notification tracking (informational, read-only)
    private LocalDateTime preReminderSentAt;
    private LocalDateTime dayOfReminderSentAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
