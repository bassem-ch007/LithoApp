package com.lithoapp.drainage.dto;

import com.lithoapp.drainage.enums.DrainageStatus;
import com.lithoapp.drainage.enums.DrainageType;
import lombok.Data;

/**
 * Query parameters for filtering drainages on GET /api/drainages.
 * All fields are optional — omitting a field means "no filter on that dimension".
 */
@Data
public class DrainageFilterRequest {

    /**
     * Filter by episode — used by the episode detail screen to load all
     * drainage records for a specific stone case.
     */
    private Long episodeId;

    /**
     * Filter by patient — used for the patient timeline view.
     * Returns all drainages across all episodes of a patient.
     */
    private Long patientId;

    private DrainageType drainageType;
    private DrainageStatus status;

    /**
     * When true, return only drainages that are currently overdue
     * (ACTIVE and plannedRemovalDate < today).
     */
    private Boolean overdue;
}
