package com.lithoapp.analysis.exception;

/**
 * Thrown when the referenced episode does not exist in the episode-service.
 *
 * In the current phase this is raised by {@link com.lithoapp.analysis.service.validation.StubEpisodeValidationService}
 * only if the stub is configured to simulate a missing episode.
 *
 * When OpenFeign is activated, the real {@code FeignEpisodeValidationService}
 * will raise this exception on a 404 response from the episode-service.
 *
 * HTTP mapping: 404 Not Found (see GlobalExceptionHandler).
 */
public class EpisodeNotFoundException extends RuntimeException {

    public EpisodeNotFoundException(Long episodeId) {
        super("Episode not found with id: " + episodeId);
    }
}
