package com.lithoapp.analysis.service.validation;

/**
 * Cross-service episode consistency validation.
 *
 * Enforces the domain rule: an analysis request must belong to an episode,
 * and that episode must belong to the same patient supplied in the request.
 *
 * Active implementation: {@link EpisodeValidationServiceImpl} (RestTemplate, @Primary).
 * Fallback: {@link StubEpisodeValidationService} (no-op, for local runs without episode-service).
 *
 * To migrate to Feign when Eureka is enabled:
 * 1. Add spring-cloud-starter-openfeign to pom.xml.
 * 2. Add @EnableFeignClients to AnalysisServiceApplication.
 * 3. Replace {@link com.lithoapp.analysis.client.HttpEpisodeServiceClient} with a @FeignClient.
 * 4. No other changes needed — EpisodeValidationServiceImpl and this interface remain unchanged.
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
