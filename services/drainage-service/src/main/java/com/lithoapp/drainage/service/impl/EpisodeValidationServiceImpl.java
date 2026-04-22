package com.lithoapp.drainage.service.impl;

import com.lithoapp.drainage.client.EpisodeServiceClient;
import com.lithoapp.drainage.dto.client.EpisodeResponse;
import com.lithoapp.drainage.exception.EpisodeNotFoundException;
import com.lithoapp.drainage.exception.EpisodePatientMismatchException;
import com.lithoapp.drainage.service.EpisodeValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Real implementation of {@link EpisodeValidationService}.
 *
 * <p>Calls episode-service via {@link EpisodeServiceClient} and enforces two rules:
 * <ol>
 *   <li>The episode must exist → {@link EpisodeNotFoundException} → HTTP 404.</li>
 *   <li>The episode must belong to the given patient →
 *       {@link EpisodePatientMismatchException} → HTTP 422.</li>
 * </ol>
 *
 * <p>{@code @Primary} ensures this bean is injected wherever
 * {@link EpisodeValidationService} is required, overriding
 * {@link StubEpisodeValidationService} that stays on the classpath as a fallback.
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class EpisodeValidationServiceImpl implements EpisodeValidationService {

    private final EpisodeServiceClient episodeServiceClient;

    @Override
    public void validateEpisodeBelongsToPatient(Long episodeId, Long patientId) {
        log.debug("Validating episodeId={} belongs to patientId={} via episode-service",
                episodeId, patientId);

        EpisodeResponse episode = episodeServiceClient.findById(episodeId)
                .orElseThrow(() -> {
                    log.warn("Drainage creation rejected — episode {} not found", episodeId);
                    return new EpisodeNotFoundException(episodeId);
                });

        if (!episode.getPatientId().equals(patientId)) {
            log.warn("Drainage creation rejected — episode {} belongs to patient {}, not patient {}",
                    episodeId, episode.getPatientId(), patientId);
            throw new EpisodePatientMismatchException(episodeId, patientId);
        }

        log.debug("Episode {} validated: exists and belongs to patient {}", episodeId, patientId);
    }
}
