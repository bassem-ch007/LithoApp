package com.lithoapp.analysis.exception;

/**
 * Thrown when the provided episodeId and patientId do not refer to the same case.
 *
 * Domain rule: every analysis request must belong to a specific episode, and that
 * episode must belong to the patient supplied in the request.
 * Accepting a mismatched pair would silently corrupt the patient → episode → analysis chain.
 *
 * HTTP mapping: 422 Unprocessable Entity (see GlobalExceptionHandler).
 */
public class EpisodePatientMismatchException extends RuntimeException {

    public EpisodePatientMismatchException(Long episodeId, Long patientId) {
        super(String.format(
                "Episode %d does not belong to patient %d. " +
                "An analysis request must be linked to an episode that belongs to the same patient.",
                episodeId, patientId));
    }
}
