package com.lithoapp.patientservice.client;

/**
 * Abstraction over the remote episode-service HTTP API.
 *
 * <p>Current implementation: {@link HttpEpisodeServiceClient} — direct
 * RestTemplate call to {@code episode-service.base-url}/episodes/patient/{id}/exists.
 *
 * <p>Future: swap for a Feign-annotated interface once service discovery
 * (Eureka) is enabled. No other changes needed — the interface contract is identical.
 */
public interface EpisodeServiceClient {

    /**
     * Returns {@code true} if at least one episode exists for the given patient.
     *
     * @param patientId the patient to check
     * @throws com.lithoapp.patientservice.exception.EpisodeServiceUnavailableException
     *         if episode-service cannot be reached or returns an unexpected error
     */
    boolean hasEpisodes(Long patientId);
}
