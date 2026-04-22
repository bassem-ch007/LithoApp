package com.lithoapp.episodeservice.service.impl;

import com.lithoapp.episodeservice.service.PatientValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * No-op stub — patient validation is bypassed entirely.
 *
 * <p>This bean remains on the classpath but is <b>never injected</b> at runtime
 * because {@link PatientValidationServiceImpl} carries {@code @Primary}.
 *
 * <p>Kept for two purposes:
 * <ol>
 *   <li>Integration / unit tests that want to skip real HTTP calls can annotate
 *       with {@code @MockBean(PatientValidationService.class)} or swap the primary.</li>
 *   <li>Quick local runs without a live patient-service: temporarily remove
 *       {@code @Primary} from {@link PatientValidationServiceImpl} to fall back here.</li>
 * </ol>
 */
@Slf4j
@Service
public class StubPatientValidationService implements PatientValidationService {

    @Override
    public void validatePatientExists(Long patientId) {
        log.debug("[STUB] Patient validation bypassed for patientId={}. " +
                  "PatientValidationServiceImpl (@Primary) should be active in production.", patientId);
    }
}
