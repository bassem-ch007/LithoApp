package com.lithoapp.patientservice.client;

/**
 * Abstraction over the remote episode-service HTTP API.
 *
 * <p>Current implementation: {@link FeignEpisodeServiceClient} — thin adapter over
 * {@link EpisodeExistsFeignClient} that translates Feign errors into
 * {@link com.lithoapp.patientservice.exception.EpisodeServiceUnavailableException}.
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
