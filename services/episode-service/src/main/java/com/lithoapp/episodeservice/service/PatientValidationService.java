package com.lithoapp.episodeservice.service;

/**
 * Validates that a patient is eligible to have episodes created for them.
 *
 * <p>Active implementation: {@link com.lithoapp.episodeservice.service.impl.PatientValidationServiceImpl}
 * — calls patient-service via {@link com.lithoapp.episodeservice.client.HttpPatientServiceClient}.
 *
 * <p>Fallback (no-op): {@link com.lithoapp.episodeservice.service.impl.StubPatientValidationService}
 * — always passes, useful for local runs without a live patient-service.
 *
 * <p>How to migrate to OpenFeign when service discovery is enabled:
 * <ol>
 *   <li>Uncomment the OpenFeign dependency in {@code pom.xml}.</li>
 *   <li>Add {@code @EnableFeignClients} to {@code EpisodeServiceApplication}.</li>
 *   <li>Annotate a Feign {@code PatientServiceClient} interface with {@code @FeignClient}.</li>
 *   <li>Remove {@code HttpPatientServiceClient} (or keep behind a profile).</li>
 *   <li>No changes needed in {@code PatientValidationServiceImpl} — it only depends on the
 *       {@code PatientServiceClient} interface.</li>
 * </ol>
 */
public interface PatientValidationService {

    /**
     * Asserts that the patient identified by {@code patientId} exists in patient-service.
     *
     * @param patientId the patient to validate
     * @throws com.lithoapp.episodeservice.exception.PatientNotFoundException if the patient does not exist
     */
    void validatePatientExists(Long patientId);
}
