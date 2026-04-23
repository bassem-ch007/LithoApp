package com.lithoapp.patientservice.client;

import com.lithoapp.patientservice.exception.EpisodeServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate-based implementation of {@link EpisodeServiceClient}.
 *
 * <p>Calls {@code GET {episode-service.base-url}/episodes/patient/{patientId}/exists}.
 * Throws {@link EpisodeServiceUnavailableException} on network errors or unexpected
 * HTTP status codes, which the global handler maps to HTTP 503.
 *
 * <p>When Feign + Eureka are enabled, replace this class with a
 * {@code @FeignClient}-annotated interface and annotate it {@code @Primary}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpEpisodeServiceClient implements EpisodeServiceClient {

    private final RestTemplate restTemplate;

    @Value("${episode-service.base-url}")
    private String baseUrl;

    @Override
    public boolean hasEpisodes(Long patientId) {
        String url = baseUrl + "/episodes/patient/" + patientId + "/exists";
        log.debug("Checking episode existence via episode-service: GET {}", url);

        try {
            Boolean result = restTemplate.getForObject(url, Boolean.class);
            return Boolean.TRUE.equals(result);

        } catch (HttpClientErrorException e) {
            log.error("episode-service returned {} for patientId={}: {}",
                    e.getStatusCode(), patientId, e.getMessage());
            throw new EpisodeServiceUnavailableException(
                    "Unexpected response from episode-service (status " + e.getStatusCode() + "). " +
                    "Please try again later.");

        } catch (RestClientException e) {
            log.error("Could not reach episode-service for patientId={}: {}", patientId, e.getMessage());
            throw new EpisodeServiceUnavailableException(
                    "Episode service is currently unavailable. Please try again later.");
        }
    }
}
