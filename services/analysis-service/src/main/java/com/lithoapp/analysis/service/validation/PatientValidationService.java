package com.lithoapp.analysis.service.validation;

/**
 * Cross-service patient existence validation.
 *
 * Active implementation: {@link PatientValidationServiceImpl} (RestTemplate, @Primary).
 * Fallback: {@link StubPatientValidationService} (no-op, for local runs without patient-service).
 *
 * To migrate to Feign when Eureka is enabled:
 * 1. Add spring-cloud-starter-openfeign to pom.xml.
 * 2. Add @EnableFeignClients to AnalysisServiceApplication.
 * 3. Replace {@link com.lithoapp.analysis.client.HttpPatientServiceClient} with a @FeignClient.
 * 4. No other changes needed — PatientValidationServiceImpl and this interface remain unchanged.
 */
public interface PatientValidationService {

    /**
     * Verifies that a patient with the given ID exists in patient-service.
     *
     * @param patientId the patient identifier to validate
     * @throws com.lithoapp.analysis.exception.PatientNotFoundException if the patient does not exist
     */
    void validatePatientExists(Long patientId);
}
