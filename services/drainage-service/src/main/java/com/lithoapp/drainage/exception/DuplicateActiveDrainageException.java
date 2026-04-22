package com.lithoapp.drainage.exception;

import com.lithoapp.drainage.enums.DrainageSide;
import com.lithoapp.drainage.enums.DrainageType;

public class DuplicateActiveDrainageException extends RuntimeException {

    /**
     * Thrown when an episode already has an ACTIVE drainage of the same type and side.
     * The duplicate guard operates at episode scope — not patient scope —
     * because a patient can legitimately have the same type/side across different stone episodes.
     */
    public DuplicateActiveDrainageException(Long episodeId, DrainageType type, DrainageSide side) {
        super(String.format(
                "Episode %d already has an ACTIVE %s drainage on the %s side.",
                episodeId, type, side
        ));
    }
}
