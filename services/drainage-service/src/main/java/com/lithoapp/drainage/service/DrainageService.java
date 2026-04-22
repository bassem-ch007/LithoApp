package com.lithoapp.drainage.service;

import com.lithoapp.drainage.dto.*;

import java.util.List;
import java.util.UUID;

public interface DrainageService {

    DrainageResponse createDrainage(CreateDrainageRequest request);

    DrainageResponse updateDrainage(UUID id, UpdateDrainageRequest request);

    DrainageResponse removeDrainage(UUID id, RemoveDrainageRequest request);

    DrainageResponse getDrainageById(UUID id);

    /**
     * Returns all drainages for a given episode.
     * This is the primary read operation — drives the episode detail screen.
     */
    List<DrainageResponse> getDrainagesByEpisodeId(Long episodeId);

    /**
     * Returns all drainages for a patient across all episodes.
     * Used for patient-level overview and reporting.
     */
    List<DrainageResponse> getDrainagesByPatientId(Long patientId);

    /**
     * Flexible filtered list — supports episodeId, patientId, type, status, overdue.
     */
    List<DrainageResponse> getDrainages(DrainageFilterRequest filter);
}
