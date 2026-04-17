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

    private Long patientId;
    private DrainageType drainageType;
    private DrainageStatus status;

    /**
     * When true, return only drainages that are currently overdue
     * (ACTIVE and plannedRemovalDate < today).
     */
    private Boolean overdue;
}
