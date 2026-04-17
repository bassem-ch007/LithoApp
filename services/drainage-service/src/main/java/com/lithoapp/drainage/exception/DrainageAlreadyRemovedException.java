package com.lithoapp.drainage.exception;

import java.util.UUID;

public class DrainageAlreadyRemovedException extends RuntimeException {

    public DrainageAlreadyRemovedException(UUID id) {
        super("Drainage with id " + id + " has already been removed.");
    }
}
