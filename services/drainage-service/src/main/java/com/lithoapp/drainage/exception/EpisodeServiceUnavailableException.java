package com.lithoapp.drainage.exception;

/**
 * Thrown when episode-service cannot be reached or returns an unexpected
 * error during episode validation.
 *
 * Maps to HTTP 503 Service Unavailable — the caller should retry later.
 */
public class EpisodeServiceUnavailableException extends RuntimeException {

    public EpisodeServiceUnavailableException(String message) {
        super(message);
    }
}
