package com.lithoapp.drainage.client;

import com.lithoapp.drainage.dto.client.EpisodeResponse;

import java.util.Optional;

/**
 * Abstraction over the remote episode-service HTTP API.
 *
 * <p>Returns {@link Optional} so callers decide how to handle a missing
 * episode without catching exceptions at the call site.
 *
 * <p>Current implementation: {@link HttpEpisodeServiceClient} — direct
 * RestTemplate call to {@code episode-service.base-url}/episodes/{id}.
 *
 * <p>Future: swap for a Feign-annotated interface once Eureka is enabled.
 * No other changes needed — the interface contract is identical.
 */
public interface EpisodeServiceClient {

    /**
     * Fetches an episode record by its primary key.
     *
     * @param episodeId the episode to look up
     * @return the episode data, or {@code Optional.empty()} on HTTP 404
     * @throws com.lithoapp.drainage.exception.EpisodeServiceUnavailableException
     *         if episode-service cannot be reached or returns an unexpected error
     */
    Optional<EpisodeResponse> findById(Long episodeId);
}
