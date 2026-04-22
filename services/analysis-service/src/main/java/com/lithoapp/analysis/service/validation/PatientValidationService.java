package com.lithoapp.analysis.service.validation;

/**
 * Extension point for cross-service patient validation.
 *
 * Current implementation: {@link StubPatientValidationService} (no-op).
 *
 * To activate real validation via OpenFeign:
 *
 * 1. Add the spring-cloud-starter-openfeign dependency to pom.xml.
 * 2. Add @EnableFeignClients to AnalysisServiceApplication.
 * 3. Create a Feign client interface:
 *
 *    @FeignClient(name = "patient-service", url = "${services.patient-service.url}")
 *    public interface PatientServiceClient {
 *        @GetMapping("/api/patients/{id}/exists")
 *        boolean existsById(@PathVariable Long id);
 *    }
 *
 * 4. Implement this interface using the Feign client, annotate with @Service @Primary.
 *    The stub will automatically be replaced.
 * 5. Throw PatientNotFoundException (or a suitable exception) when the patient is not found.
 */
public interface PatientValidationService {

    /**
     * Verifies that a patient with the given ID exists and is active in the patient-service.
     *
     * @param patientId the patient identifier to validate
     * @throws com.lithoapp.analysis.exception.EpisodeNotFoundException or a dedicated
     *         PatientNotFoundException when the patient cannot be found (future Feign impl)
     */
    void validatePatientExists(Long patientId);
}
