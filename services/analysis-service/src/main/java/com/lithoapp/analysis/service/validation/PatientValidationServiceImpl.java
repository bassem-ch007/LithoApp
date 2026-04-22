package com.lithoapp.analysis.service.validation;

import com.lithoapp.analysis.client.PatientServiceClient;
import com.lithoapp.analysis.dto.client.PatientResponse;
import com.lithoapp.analysis.exception.PatientInactiveException;
import com.lithoapp.analysis.exception.PatientNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Real implementation of {@link PatientValidationService}.
 *
 * <p>Calls patient-service via {@link PatientServiceClient} and enforces two rules:
 * <ol>
 *   <li>The patient must exist → {@link PatientNotFoundException} → HTTP 404.</li>
 *   <li>The patient must be active → {@link PatientInactiveException} → HTTP 422.</li>
 * </ol>
 *
 * <p>{@code @Primary} ensures this bean is injected wherever
 * {@link PatientValidationService} is required, overriding
 * {@link StubPatientValidationService} that stays on the classpath as a fallback.
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
                    log.warn("Analysis request rejected — patient {} not found", patientId);
                    return new PatientNotFoundException(patientId);
                });

        if (!patient.isActive()) {
            log.warn("Analysis request rejected — patient {} is inactive", patientId);
            throw new PatientInactiveException(patientId);
        }

        log.debug("Patient {} validated: exists and active", patientId);
    }
}
