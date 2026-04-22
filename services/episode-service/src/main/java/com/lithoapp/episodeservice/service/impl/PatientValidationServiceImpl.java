package com.lithoapp.episodeservice.service.impl;

import com.lithoapp.episodeservice.client.PatientServiceClient;
import com.lithoapp.episodeservice.dto.client.PatientResponse;
import com.lithoapp.episodeservice.exception.PatientInactiveException;
import com.lithoapp.episodeservice.exception.PatientNotFoundException;
import com.lithoapp.episodeservice.service.PatientValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Real implementation of {@link PatientValidationService}.
 *
 * <p>Calls patient-service via {@link PatientServiceClient} and enforces two rules:
 * <ol>
 *   <li>The patient must exist (404 from patient-service → {@link PatientNotFoundException} → HTTP 404).</li>
 *   <li>The patient must be active ({@code active=false} → {@link PatientInactiveException} → HTTP 422).</li>
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

        PatientResponse patient = patientServiceClient.findById(patientId)
                .orElseThrow(() -> {
                    log.warn("Episode creation rejected — patient {} not found", patientId);
                    return new PatientNotFoundException(patientId);
                });

        if (!patient.isActive()) {
            log.warn("Episode creation rejected — patient {} is inactive", patientId);
            throw new PatientInactiveException(patientId);
        }

        log.debug("Patient {} validated: exists and active", patientId);
    }
}
