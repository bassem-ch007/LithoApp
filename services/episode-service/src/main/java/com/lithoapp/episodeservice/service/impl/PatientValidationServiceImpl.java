package com.lithoapp.episodeservice.service.impl;

import com.lithoapp.episodeservice.client.PatientServiceClient;
import com.lithoapp.episodeservice.exception.PatientNotFoundException;
import com.lithoapp.episodeservice.service.PatientValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Real implementation of {@link PatientValidationService}.
 *
 * <p>Calls patient-service via {@link PatientServiceClient} and enforces one rule:
 * <ol>
 *   <li>The patient must exist (404 from patient-service → {@link PatientNotFoundException} → HTTP 404).</li>
 * </ol>
 *
 * <p>Annotated {@code @Primary} so Spring injects this bean wherever
 * {@link PatientValidationService} is required, overriding the
 * {@link StubPatientValidationService} that remains on the classpath.
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class PatientValidationServiceImpl implements PatientValidationService {

    private final PatientServiceClient patientServiceClient;

    @Override
    public void validatePatientExists(Long patientId) {
        log.debug("Validating patient patientId={} via patient-service", patientId);

        patientServiceClient.findById(patientId)
                .orElseThrow(() -> {
                    log.warn("Episode creation rejected — patient {} not found", patientId);
                    return new PatientNotFoundException(patientId);
                });

        log.debug("Patient {} validated: exists", patientId);
    }
}
