package com.lithoapp.analysis.client;

import com.lithoapp.analysis.dto.client.EpisodeResponse;

import java.util.Optional;

/**
 * Abstraction over the remote episode-service HTTP API.
 *
 * <p>Returns {@link Optional} so callers decide how to handle a missing episode
 * without catching exceptions at the call site.
 *
 * <p>Current implementation: {@link FeignEpisodeServiceClient} — thin adapter over
 * {@link EpisodeFeignClient} that translates {@code feign.FeignException.NotFound}
 * into {@code Optional.empty()} and other Feign errors into
 * {@link com.lithoapp.analysis.exception.EpisodeServiceUnavailableException}.
 */
public interface EpisodeServiceClient {

    /**
     * Fetches an episode record by its primary key.
     *
     * @param episodeId the episode to look up
     * @return the episode data, or {@code Optional.empty()} on HTTP 404
     * @throws com.lithoapp.analysis.exception.EpisodeServiceUnavailableException
     *         if episode-service cannot be reached or returns an unexpected error
     */
    Optional<EpisodeResponse> findById(Long episodeId);
}
