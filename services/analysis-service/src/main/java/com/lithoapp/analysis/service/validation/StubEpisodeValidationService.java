package com.lithoapp.analysis.service.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * No-op stub for {@link EpisodeValidationService}.
 *
 * Performs no HTTP call — assumes all (episodeId, patientId) pairs are valid.
 * Overridden by {@link EpisodeValidationServiceImpl} which is annotated {@code @Primary}
 * and calls episode-service over HTTP via RestTemplate.
 *
 * Kept on the classpath as a fallback. To disable real validation locally,
 * remove {@code @Primary} from {@link EpisodeValidationServiceImpl}.
 */
@Slf4j
@Service
public class StubEpisodeValidationService implements EpisodeValidationService {

    @Override
    public void validateEpisodeBelongsToPatient(Long episodeId, Long patientId) {
        log.debug("[STUB] Episode-patient consistency check bypassed " +
                "(episodeId={}, patientId={}). " +
                "Wire FeignEpisodeValidationService to enforce real checks.", episodeId, patientId);
    }
}
