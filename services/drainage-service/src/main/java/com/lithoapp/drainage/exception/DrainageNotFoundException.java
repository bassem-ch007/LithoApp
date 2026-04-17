package com.lithoapp.drainage.exception;

import java.util.UUID;

public class DrainageNotFoundException extends RuntimeException {

    public DrainageNotFoundException(UUID id) {
        super("Drainage not found with id: " + id);
    }
}
