package com.lithoapp.drainage.client;

import com.lithoapp.drainage.dto.client.EpisodeResponse;
import com.lithoapp.drainage.exception.EpisodeServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * RestTemplate-based implementation of {@link EpisodeServiceClient}.
 *
 * <p>Calls {@code GET {episode-service.base-url}/episodes/{id}}.
 * <ul>
 *   <li>HTTP 200 → returns the deserialized {@link EpisodeResponse}</li>
 *   <li>HTTP 404 → returns {@code Optional.empty()}</li>
 *   <li>Network error / unexpected status → throws {@link EpisodeServiceUnavailableException}</li>
 * </ul>
 *
 * <p>When Feign + Eureka are enabled, replace this with a {@code @FeignClient}
 * interface and mark it {@code @Primary}. Delete this class (or profile-gate it).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpEpisodeServiceClient implements EpisodeServiceClient {

    private final RestTemplate restTemplate;

    @Value("${episode-service.base-url}")
    private String baseUrl;

    @Override
    public Optional<EpisodeResponse> findById(Long episodeId) {
        String url = baseUrl + "/episodes/" + episodeId;
        log.debug("Fetching episode from episode-service: GET {}", url);

        try {
            ResponseEntity<EpisodeResponse> response =
                    restTemplate.getForEntity(url, EpisodeResponse.class);
            return Optional.ofNullable(response.getBody());

        } catch (HttpClientErrorException.NotFound e) {
            log.debug("episode-service returned 404 for episodeId={}", episodeId);
            return Optional.empty();

        } catch (HttpClientErrorException e) {
            log.error("episode-service returned {} for episodeId={}: {}",
                    e.getStatusCode(), episodeId, e.getMessage());
            throw new EpisodeServiceUnavailableException(
                    "Unexpected response from episode-service (HTTP " + e.getStatusCode() + "). " +
                    "Please try again later.");

        } catch (RestClientException e) {
            log.error("Could not reach episode-service for episodeId={}: {}", episodeId, e.getMessage());
            throw new EpisodeServiceUnavailableException(
                    "Episode service is currently unavailable. Please try again later.");
        }
    }
}
