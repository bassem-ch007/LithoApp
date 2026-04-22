package com.lithoapp.drainage.service.impl;

import com.lithoapp.drainage.service.PatientValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * No-op stub for {@link PatientValidationService}.
 *
 * <p>This bean remains on the classpath but is <b>never injected</b> at runtime
 * because {@link PatientValidationServiceImpl} carries {@code @Primary}.
 *
 * <p>Kept for two purposes:
 * <ol>
 *   <li>Tests can swap it in with {@code @MockBean} or by removing {@code @Primary}
 *       from the real implementation.</li>
 *   <li>Local runs without a live patient-service: temporarily remove {@code @Primary}
 *       from {@link PatientValidationServiceImpl} to fall back here.</li>
 * </ol>
 */
@Slf4j
@Service
public class StubPatientValidationService implements PatientValidationService {

    @Override
    public void validatePatientExists(Long patientId) {
        log.debug("[STUB] Patient validation bypassed for patientId={}. " +
                  "PatientValidationServiceImpl (@Primary) is active in production.", patientId);
    }
}
