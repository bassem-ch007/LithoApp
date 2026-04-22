package com.lithoapp.drainage.exception;

/**
 * Thrown by EpisodeValidationService when a referenced episode does not exist.
 * Currently unused by the stub implementation — will be thrown by FeignEpisodeValidationService.
 */
public class EpisodeNotFoundException extends RuntimeException {

    public EpisodeNotFoundException(Long episodeId) {
        super("Episode not found with id: " + episodeId);
    }
}
