package com.lithoapp.episodeservice.exception;

public class EpisodeNotFoundException extends RuntimeException {

    public EpisodeNotFoundException(Long id) {
        super("Episode not found with id: " + id);
    }
}
