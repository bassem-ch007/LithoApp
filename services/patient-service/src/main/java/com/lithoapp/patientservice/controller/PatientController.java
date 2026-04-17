package com.lithoapp.patientservice.controller;

import com.lithoapp.patientservice.dto.request.CreatePatientRequest;
import com.lithoapp.patientservice.dto.request.UpdatePatientRequest;
import com.lithoapp.patientservice.dto.response.PatientResponse;
import com.lithoapp.patientservice.dto.response.PatientSummaryResponse;
import com.lithoapp.patientservice.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Tag(name = "Patient", description = "Patient management endpoints")
public class PatientController {

    private final PatientService patientService;

    // ── Create ────────────────────────────────────────────────────────────────

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new patient with full clinical profile")
    public ResponseEntity<PatientResponse> createPatient(
            @Valid @RequestBody CreatePatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.createPatient(request));
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a patient by internal ID")
    public ResponseEntity<PatientResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/by-di/{di}")
    @Operation(summary = "Retrieve a patient by DI (Dossier d'Identité)")
    public ResponseEntity<PatientResponse> getByDi(@PathVariable String di) {
        return ResponseEntity.ok(patientService.getPatientByDi(di));
    }

    @GetMapping("/by-dmi/{dmi}")
    @Operation(summary = "Retrieve a patient by DMI (Dossier Médical Informatisé)")
    public ResponseEntity<PatientResponse> getByDmi(@PathVariable String dmi) {
        return ResponseEntity.ok(patientService.getPatientByDmi(dmi));
    }

    // ── Search ────────────────────────────────────────────────────────────────

    @GetMapping("/search")
    @Operation(summary = "Search patients — filter by di, dmi, name (first+last) or phone")
    public ResponseEntity<Page<PatientSummaryResponse>> search(
            @Parameter(description = "Filter by DI (partial match)")
            @RequestParam(required = false) String di,
            @Parameter(description = "Filter by DMI (partial match)")
            @RequestParam(required = false) String dmi,
            @Parameter(description = "Filter by first name or last name (partial match)")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filter by phone (partial match)")
            @RequestParam(required = false) String phone,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
        return ResponseEntity.ok(patientService.searchPatients(di, dmi, name, phone, pageable));
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @PutMapping("/{id}")
    @Operation(summary = "Update a patient's data")
    public ResponseEntity<PatientResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientRequest request) {
        return ResponseEntity.ok(patientService.updatePatient(id, request));
    }

    // ── Soft-delete ───────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate a patient (soft delete)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
