package com.lithoapp.drainage.exception;

import com.lithoapp.drainage.enums.DrainageSide;
import com.lithoapp.drainage.enums.DrainageType;

public class DuplicateActiveDrainageException extends RuntimeException {

    public DuplicateActiveDrainageException(Long patientId, DrainageType type, DrainageSide side) {
        super(String.format(
            "Patient %d already has an ACTIVE %s drainage on the %s side.",
            patientId, type, side
        ));
    }
}
