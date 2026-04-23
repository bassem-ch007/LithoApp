package com.lithoapp.drainage.service.impl;

import com.lithoapp.drainage.service.EpisodeValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Development stub — episode validation is not performed against episode-service.
 *
 * Overridden by {@link EpisodeValidationServiceImpl} ({@code @Primary}), which
 * calls episode-service via OpenFeign.
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
                "EpisodeValidationServiceImpl is @Primary and should override this stub.", episodeId, patientId);
    }
}
