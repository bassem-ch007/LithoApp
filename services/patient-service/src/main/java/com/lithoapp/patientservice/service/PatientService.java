package com.lithoapp.patientservice.service;

import com.lithoapp.patientservice.dto.request.CreatePatientRequest;
import com.lithoapp.patientservice.dto.request.UpdatePatientRequest;
import com.lithoapp.patientservice.dto.response.PatientResponse;
import com.lithoapp.patientservice.dto.response.PatientSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {

    PatientResponse createPatient(CreatePatientRequest request);

    PatientResponse getPatientById(Long id);

    PatientResponse getPatientByDi(String di);

    PatientResponse getPatientByDmi(String dmi);

    PatientResponse updatePatient(Long id, UpdatePatientRequest request);

    void deletePatient(Long id);

    Page<PatientSummaryResponse> searchPatients(String di, String dmi, String name, String phone, Pageable pageable);
}
