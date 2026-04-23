package com.lithoapp.analysis.exception;

/**
 * Thrown when the referenced episode does not exist in episode-service.
 *
 * Raised by {@link com.lithoapp.analysis.service.validation.EpisodeValidationServiceImpl}
 * on a 404 response from episode-service.
 *
 * HTTP mapping: 404 Not Found (see GlobalExceptionHandler).
 */
public class EpisodeNotFoundException extends RuntimeException {

    public EpisodeNotFoundException(Long episodeId) {
        super("Episode not found with id: " + episodeId);
    }
}
