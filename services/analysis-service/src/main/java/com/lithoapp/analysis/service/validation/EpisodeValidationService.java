package com.lithoapp.analysis.service.validation;

/**
 * Extension point for cross-service episode consistency validation.
 *
 * Enforces the core domain rule: an analysis request must belong to an episode,
 * and that episode must belong to the same patient supplied in the request.
 *
 * Current implementation: {@link StubEpisodeValidationService} (no-op).
 *
 * To activate real validation via OpenFeign:
 *
 * 1. Add the spring-cloud-starter-openfeign dependency to pom.xml.
 * 2. Add @EnableFeignClients to AnalysisServiceApplication.
 * 3. Create a Feign client interface targeting the episode-service:
 *
 *    @FeignClient(name = "episode-service", url = "${services.episode-service.url}")
 *    public interface EpisodeServiceClient {
 *        @GetMapping("/episodes/{id}")
 *        EpisodeResponse getById(@PathVariable Long id);
 *    }
 *
 * 4. Implement this interface:
 *    - If the Feign call returns 404, throw EpisodeNotFoundException(episodeId).
 *    - If episode.patientId != patientId, throw EpisodePatientMismatchException(episodeId, patientId).
 * 5. Annotate the real impl with @Service @Primary — the stub is replaced automatically.
 */
public interface EpisodeValidationService {

    /**
     * Verifies that:
     * 1. The episode with the given {@code episodeId} exists in the episode-service.
     * 2. That episode belongs to the patient identified by {@code patientId}.
     *
     * @param episodeId the episode to look up
     * @param patientId the patient the caller claims owns that episode
     * @throws com.lithoapp.analysis.exception.EpisodeNotFoundException      if the episode does not exist
     * @throws com.lithoapp.analysis.exception.EpisodePatientMismatchException if the episode belongs to a different patient
     */
    void validateEpisodeBelongsToPatient(Long episodeId, Long patientId);
}
