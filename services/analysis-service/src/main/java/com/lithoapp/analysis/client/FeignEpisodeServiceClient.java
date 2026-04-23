package com.lithoapp.analysis.client;

import com.lithoapp.analysis.dto.client.EpisodeResponse;
import com.lithoapp.analysis.exception.EpisodeServiceUnavailableException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Feign-backed implementation of {@link EpisodeServiceClient}.
 *
 * <p>Delegates to {@link EpisodeFeignClient} and preserves the legacy semantics:
 * <ul>
 *   <li>HTTP 200 → returns the deserialized {@link EpisodeResponse}</li>
 *   <li>HTTP 404 → returns {@code Optional.empty()}</li>
 *   <li>Any other Feign error (network / 5xx / unexpected 4xx) →
 *       throws {@link EpisodeServiceUnavailableException} (mapped to HTTP 503).</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FeignEpisodeServiceClient implements EpisodeServiceClient {

    private final EpisodeFeignClient episodeFeignClient;

    @Override
    public Optional<EpisodeResponse> findById(Long episodeId) {
        log.debug("Fetching episode from episode-service via Feign: episodeId={}", episodeId);

        try {
            return Optional.ofNullable(episodeFeignClient.getById(episodeId));

        } catch (FeignException.NotFound e) {
            log.debug("episode-service returned 404 for episodeId={}", episodeId);
            return Optional.empty();

        } catch (FeignException e) {
            log.error("episode-service call failed for episodeId={} (status {}): {}",
                    episodeId, e.status(), e.getMessage());
            throw new EpisodeServiceUnavailableException(
                    "Episode service is currently unavailable. Please try again later.");
        }
    }
}
