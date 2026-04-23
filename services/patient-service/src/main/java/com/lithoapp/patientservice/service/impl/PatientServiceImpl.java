package com.lithoapp.patientservice.service.impl;

import com.lithoapp.patientservice.client.EpisodeServiceClient;
import com.lithoapp.patientservice.dto.request.CreatePatientRequest;
import com.lithoapp.patientservice.dto.request.UpdatePatientRequest;
import com.lithoapp.patientservice.dto.response.PatientResponse;
import com.lithoapp.patientservice.dto.response.PatientSummaryResponse;
import com.lithoapp.patientservice.entity.*;
import com.lithoapp.patientservice.exception.DuplicateIdentifierException;
import com.lithoapp.patientservice.exception.PatientDeletionNotAllowedException;
import com.lithoapp.patientservice.exception.PatientNotFoundException;
import com.lithoapp.patientservice.mapper.PatientMapper;
import com.lithoapp.patientservice.repository.PatientRepository;
import com.lithoapp.patientservice.service.PatientService;
import com.lithoapp.patientservice.specification.PatientSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final EpisodeServiceClient episodeServiceClient;

    // ── Create ────────────────────────────────────────────────────────────────

    @Override
    public PatientResponse createPatient(CreatePatientRequest request) {
        validateUniqueIdentifiers(request.getDi(), request.getDmi());

        Patient patient = patientMapper.toEntity(request);

        // Clinical info
        if (request.getClinicalInfo() != null) {
            ClinicalInfo clinicalInfo = patientMapper.toClinicalInfo(request.getClinicalInfo());
            patient.setClinicalInfo(clinicalInfo);
        }

        // Medication
        if (request.getMedication() != null) {
            Medication medication = patientMapper.toMedication(request.getMedication());
            patient.setMedication(medication);
        }

        // Associated diseases
        if (request.getAssociatedDiseases() != null) {
            List<AssociatedDisease> diseases = request.getAssociatedDiseases().stream()
                    .map(name -> AssociatedDisease.builder().name(name).patient(patient).build())
                    .toList();
            patient.getAssociatedDiseases().addAll(diseases);
        }

        // Genetic diseases
        if (request.getGeneticDiseases() != null) {
            List<GeneticDisease> genetics = request.getGeneticDiseases().stream()
                    .map(name -> GeneticDisease.builder().name(name).patient(patient).build())
                    .toList();
            patient.getGeneticDiseases().addAll(genetics);
        }

        // Anatomical anomalies
        if (request.getAnatomicalAnomalies() != null) {
            List<AnatomicalAnomaly> anomalies = request.getAnatomicalAnomalies().stream()
                    .map(name -> AnatomicalAnomaly.builder().name(name).patient(patient).build())
                    .toList();
            patient.getAnatomicalAnomalies().addAll(anomalies);
        }

        Patient saved = patientRepository.save(patient);
        return patientMapper.toResponse(saved);
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        return patientMapper.toResponse(findByIdOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientByDi(String di) {
        Patient patient = patientRepository.findByDi(di)
                .orElseThrow(() -> new PatientNotFoundException("DI", di));
        return patientMapper.toResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientByDmi(String dmi) {
        Patient patient = patientRepository.findByDmi(dmi)
                .orElseThrow(() -> new PatientNotFoundException("DMI", dmi));
        return patientMapper.toResponse(patient);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Override
    public PatientResponse updatePatient(Long id, UpdatePatientRequest request) {
        Patient patient = findByIdOrThrow(id);

        // Apply scalar fields via MapStruct (ignores null values)
        patientMapper.updatePatientFromRequest(request, patient);

        // Clinical info
        if (request.getClinicalInfo() != null) {
            if (patient.getClinicalInfo() == null) {
                ClinicalInfo clinicalInfo = patientMapper.toClinicalInfo(request.getClinicalInfo());
                patient.setClinicalInfo(clinicalInfo);
            } else {
                patientMapper.updateClinicalInfoFromRequest(request.getClinicalInfo(), patient.getClinicalInfo());
            }
        }

        // Medication
        if (request.getMedication() != null) {
            if (patient.getMedication() == null) {
                Medication medication = patientMapper.toMedication(request.getMedication());
                patient.setMedication(medication);
            } else {
                patientMapper.updateMedicationFromRequest(request.getMedication(), patient.getMedication());
            }
        }

        // Replace lists entirely when provided
        if (request.getAssociatedDiseases() != null) {
            patient.getAssociatedDiseases().clear();
            request.getAssociatedDiseases().stream()
                    .map(name -> AssociatedDisease.builder().name(name).patient(patient).build())
                    .forEach(patient.getAssociatedDiseases()::add);
        }

        if (request.getGeneticDiseases() != null) {
            patient.getGeneticDiseases().clear();
            request.getGeneticDiseases().stream()
                    .map(name -> GeneticDisease.builder().name(name).patient(patient).build())
                    .forEach(patient.getGeneticDiseases()::add);
        }

        if (request.getAnatomicalAnomalies() != null) {
            patient.getAnatomicalAnomalies().clear();
            request.getAnatomicalAnomalies().stream()
                    .map(name -> AnatomicalAnomaly.builder().name(name).patient(patient).build())
                    .forEach(patient.getAnatomicalAnomalies()::add);
        }

        return patientMapper.toResponse(patientRepository.save(patient));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Override
    public void deletePatient(Long id) {
        Patient patient = findByIdOrThrow(id);
        if (episodeServiceClient.hasEpisodes(id)) {
            throw new PatientDeletionNotAllowedException(id);
        }
        patientRepository.delete(patient);
    }

    // ── Search ────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<PatientSummaryResponse> searchPatients(
            String di, String dmi, String name, String phone, Pageable pageable) {
        return patientRepository.findAll(PatientSpecification.search(di, dmi, name, phone), pageable)
                .map(patientMapper::toSummary);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Patient findByIdOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }

    private void validateUniqueIdentifiers(String di, String dmi) {
        if (patientRepository.existsByDi(di)) {
            throw new DuplicateIdentifierException("DI", di);
        }
        if (patientRepository.existsByDmi(dmi)) {
            throw new DuplicateIdentifierException("DMI", dmi);
        }
    }
}
