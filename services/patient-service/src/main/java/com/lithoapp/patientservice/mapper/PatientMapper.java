package com.lithoapp.patientservice.mapper;

import com.lithoapp.patientservice.dto.request.ClinicalInfoRequest;
import com.lithoapp.patientservice.dto.request.CreatePatientRequest;
import com.lithoapp.patientservice.dto.request.MedicationRequest;
import com.lithoapp.patientservice.dto.request.UpdatePatientRequest;
import com.lithoapp.patientservice.dto.response.ClinicalInfoResponse;
import com.lithoapp.patientservice.dto.response.MedicationResponse;
import com.lithoapp.patientservice.dto.response.PatientResponse;
import com.lithoapp.patientservice.dto.response.PatientSummaryResponse;
import com.lithoapp.patientservice.entity.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PatientMapper {

    // ── Create ────────────────────────────────────────────────────────────────

    @Mapping(target = "id",                  ignore = true)
    @Mapping(target = "active",              constant = "true")
    @Mapping(target = "createdAt",           ignore = true)
    @Mapping(target = "updatedAt",           ignore = true)
    @Mapping(target = "clinicalInfo",        ignore = true)
    @Mapping(target = "medication",          ignore = true)
    @Mapping(target = "associatedDiseases",  ignore = true)
    @Mapping(target = "geneticDiseases",     ignore = true)
    @Mapping(target = "anatomicalAnomalies", ignore = true)
    Patient toEntity(CreatePatientRequest request);

    @Mapping(target = "id",      ignore = true)
    @Mapping(target = "patient", ignore = true)
    ClinicalInfo toClinicalInfo(ClinicalInfoRequest request);

    @Mapping(target = "id",      ignore = true)
    @Mapping(target = "patient", ignore = true)
    Medication toMedication(MedicationRequest request);

    // ── Update ────────────────────────────────────────────────────────────────

    @Mapping(target = "id",                  ignore = true)
    @Mapping(target = "di",                  ignore = true)
    @Mapping(target = "dmi",                 ignore = true)
    @Mapping(target = "createdAt",           ignore = true)
    @Mapping(target = "updatedAt",           ignore = true)
    @Mapping(target = "clinicalInfo",        ignore = true)
    @Mapping(target = "medication",          ignore = true)
    @Mapping(target = "associatedDiseases",  ignore = true)
    @Mapping(target = "geneticDiseases",     ignore = true)
    @Mapping(target = "anatomicalAnomalies", ignore = true)
    void updatePatientFromRequest(UpdatePatientRequest request, @MappingTarget Patient patient);

    @Mapping(target = "id",      ignore = true)
    @Mapping(target = "patient", ignore = true)
    void updateClinicalInfoFromRequest(ClinicalInfoRequest request, @MappingTarget ClinicalInfo clinicalInfo);

    @Mapping(target = "id",      ignore = true)
    @Mapping(target = "patient", ignore = true)
    void updateMedicationFromRequest(MedicationRequest request, @MappingTarget Medication medication);

    // ── Response ──────────────────────────────────────────────────────────────

    @Mapping(target = "associatedDiseases",  expression = "java(toNameList(patient.getAssociatedDiseases()))")
    @Mapping(target = "geneticDiseases",     expression = "java(toGeneticNameList(patient.getGeneticDiseases()))")
    @Mapping(target = "anatomicalAnomalies", expression = "java(toAnomalyNameList(patient.getAnatomicalAnomalies()))")
    PatientResponse toResponse(Patient patient);

    PatientSummaryResponse toSummary(Patient patient);

    ClinicalInfoResponse toClinicalInfoResponse(ClinicalInfo clinicalInfo);

    MedicationResponse toMedicationResponse(Medication medication);

    // ── Helpers ───────────────────────────────────────────────────────────────

    default List<String> toNameList(List<AssociatedDisease> list) {
        if (list == null) return List.of();
        return list.stream().map(AssociatedDisease::getName).toList();
    }

    default List<String> toGeneticNameList(List<GeneticDisease> list) {
        if (list == null) return List.of();
        return list.stream().map(GeneticDisease::getName).toList();
    }

    default List<String> toAnomalyNameList(List<AnatomicalAnomaly> list) {
        if (list == null) return List.of();
        return list.stream().map(AnatomicalAnomaly::getName).toList();
    }
}
