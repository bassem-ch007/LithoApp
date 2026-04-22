package com.lithoapp.drainage.service;

/**
 * Validates that a patient is eligible to have drainage records created for them.
 *
 * <p>Active implementation: {@link com.lithoapp.drainage.service.impl.PatientValidationServiceImpl}
 * — calls patient-service via {@link com.lithoapp.drainage.client.HttpPatientServiceClient}.
 *
 * <p>Fallback (no-op): {@link com.lithoapp.drainage.service.impl.StubPatientValidationService}
 * — always passes, useful for local runs without a live patient-service.
 *
 * <p>How to migrate to OpenFeign when service discovery is enabled:
 * <ol>
 *   <li>Uncomment the OpenFeign dependency in {@code pom.xml}.</li>
 *   <li>Add {@code @EnableFeignClients} to {@code DrainageServiceApplication}.</li>
 *   <li>Annotate a Feign {@code PatientServiceClient} interface with {@code @FeignClient}.</li>
 *   <li>Remove {@code HttpPatientServiceClient} (or keep behind a profile).</li>
 *   <li>No changes needed in {@code PatientValidationServiceImpl} — it only depends on the
 *       {@code PatientServiceClient} interface.</li>
 * </ol>
 */
public interface PatientValidationService {

    /**
     * Asserts that the patient identified by {@code patientId}:
     * <ul>
     *   <li>exists in patient-service → {@link com.lithoapp.drainage.exception.PatientNotFoundException} (HTTP 404)</li>
     *   <li>is currently active → {@link com.lithoapp.drainage.exception.PatientInactiveException} (HTTP 422)</li>
     * </ul>
     *
     * @param patientId the patient to validate
     */
    void validatePatientExists(Long patientId);
}
