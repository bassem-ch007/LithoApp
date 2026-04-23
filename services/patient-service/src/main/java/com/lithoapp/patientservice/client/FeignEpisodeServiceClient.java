package com.lithoapp.patientservice.client;

import com.lithoapp.patientservice.exception.EpisodeServiceUnavailableException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Feign-backed implementation of {@link EpisodeServiceClient}.
 *
 * <p>Delegates to {@link EpisodeExistsFeignClient} and preserves the legacy semantics:
 * any Feign error (network / unexpected status) is translated into
 * {@link EpisodeServiceUnavailableException}, which the global handler maps to HTTP 503.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FeignEpisodeServiceClient implements EpisodeServiceClient {

    private final EpisodeExistsFeignClient episodeExistsFeignClient;

    @Override
    public boolean hasEpisodes(Long patientId) {
        log.debug("Checking episode existence via Feign: patientId={}", patientId);

        try {
            return episodeExistsFeignClient.hasEpisodes(patientId);

        } catch (FeignException e) {
            log.error("episode-service call failed for patientId={} (status {}): {}",
                    patientId, e.status(), e.getMessage());
            throw new EpisodeServiceUnavailableException(
                    "Episode service is currently unavailable. Please try again later.");
        }
    }
}
