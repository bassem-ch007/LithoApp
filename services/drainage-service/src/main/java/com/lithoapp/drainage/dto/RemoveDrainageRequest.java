package com.lithoapp.drainage.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RemoveDrainageRequest {

    /**
     * Date on which the drainage was physically removed.
     * Must be >= placedAt (validated in service).
     * Defaults to today if not provided — handled in service.
     */
    @NotNull(message = "removedAt is required")
    private LocalDate removedAt;
}
