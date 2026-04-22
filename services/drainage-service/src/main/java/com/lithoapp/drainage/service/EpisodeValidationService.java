package com.lithoapp.drainage.service;

/**
 * Abstraction for episode consistency validation before drainage operations.
 *
 * Enforces the core domain rule: a drainage record must belong to an episode,
 * and that episode must belong to the same patient supplied in the request.
 *
 * Current implementation: {@link com.lithoapp.drainage.service.impl.StubEpisodeValidationService}
 * (no-op) is overridden by
 * {@link com.lithoapp.drainage.service.impl.EpisodeValidationServiceImpl} ({@code @Primary}),
 * which calls episode-service over HTTP via RestTemplate.
 */
public interface EpisodeValidationService {

    /**
     * Verifies that:
     * <ol>
     *   <li>The episode with the given {@code episodeId} exists in episode-service.</li>
     *   <li>That episode belongs to the patient identified by {@code patientId}.</li>
     * </ol>
     *
     * @param episodeId the episode to look up
     * @param patientId the patient the caller claims owns that episode
     * @throws com.lithoapp.drainage.exception.EpisodeNotFoundException       if the episode does not exist
     * @throws com.lithoapp.drainage.exception.EpisodePatientMismatchException if the episode belongs to a different patient
     */
    void validateEpisodeBelongsToPatient(Long episodeId, Long patientId);
}
