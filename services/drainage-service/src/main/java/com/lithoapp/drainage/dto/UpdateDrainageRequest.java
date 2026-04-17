package com.lithoapp.drainage.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * Only a subset of fields may be updated after creation.
 * Both fields are optional — send only what needs to change.
 */
@Data
public class UpdateDrainageRequest {

    /**
     * New planned removal date.
     * Must be >= placedAt of the existing drainage (validated in service).
     */
    private LocalDate plannedRemovalDate;

    private String notes;
}
