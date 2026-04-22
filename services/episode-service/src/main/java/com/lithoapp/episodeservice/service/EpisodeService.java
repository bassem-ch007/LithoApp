package com.lithoapp.episodeservice.service;

import com.lithoapp.episodeservice.dto.request.CreateEpisodeRequest;
import com.lithoapp.episodeservice.dto.request.UpdateEpisodeRequest;
import com.lithoapp.episodeservice.dto.response.EpisodeResponse;
import com.lithoapp.episodeservice.dto.response.EpisodeSummaryResponse;
import com.lithoapp.episodeservice.enums.EpisodeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EpisodeService {

    /**
     * Opens a new case folder for a patient. Initial status is always ACTIVE.
     */
    EpisodeResponse createEpisode(CreateEpisodeRequest request);

    /**
     * Returns full episode details by ID.
     */
    EpisodeResponse getEpisodeById(Long id);

    /**
     * Returns a paginated list of episode summaries for a given patient.
     *
     * @param patientId the patient whose episodes are listed
     * @param status    optional filter — null returns all episodes, ACTIVE or CLOSED filters by status
     * @param pageable  pagination and sorting parameters
     */
    Page<EpisodeSummaryResponse> getEpisodesByPatient(Long patientId, EpisodeStatus status, Pageable pageable);

    /**
     * Partially updates the episode case-folder fields (title, notes, recurrence, status).
     * patientId and openedAt are immutable after creation.
     */
    EpisodeResponse updateEpisode(Long id, UpdateEpisodeRequest request);
}
