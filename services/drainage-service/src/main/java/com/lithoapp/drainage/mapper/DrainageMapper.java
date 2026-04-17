package com.lithoapp.drainage.mapper;

import com.lithoapp.drainage.dto.CreateDrainageRequest;
import com.lithoapp.drainage.dto.DrainageResponse;
import com.lithoapp.drainage.entity.Drainage;
import com.lithoapp.drainage.enums.DrainageStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Manual mapper between Drainage entity and DTOs.
 * Can be replaced with MapStruct in a future iteration without changing
 * the service or controller layers.
 */
@Component
public class DrainageMapper {

    /**
     * Maps a CreateDrainageRequest to a new Drainage entity.
     * Status is set to ACTIVE; audit timestamps are handled by Hibernate.
     */
    public Drainage toEntity(CreateDrainageRequest request) {
        return Drainage.builder()
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .drainageType(request.getDrainageType())
                .side(request.getSide())
                .placedAt(request.getPlacedAt())
                .plannedRemovalDate(request.getPlannedRemovalDate())
                .status(DrainageStatus.ACTIVE)
                .jjType(request.getJjType())
                .notes(request.getNotes())
                .build();
    }

    /**
     * Maps a Drainage entity to a DrainageResponse DTO.
     * The overdue flag is computed here based on current date.
     */
    public DrainageResponse toResponse(Drainage drainage) {
        boolean overdue = drainage.getStatus() == DrainageStatus.ACTIVE
                && drainage.getPlannedRemovalDate() != null
                && drainage.getPlannedRemovalDate().isBefore(LocalDate.now());

        return DrainageResponse.builder()
                .id(drainage.getId())
                .patientId(drainage.getPatientId())
                .doctorId(drainage.getDoctorId())
                .drainageType(drainage.getDrainageType())
                .side(drainage.getSide())
                .placedAt(drainage.getPlacedAt())
                .plannedRemovalDate(drainage.getPlannedRemovalDate())
                .removedAt(drainage.getRemovedAt())
                .status(drainage.getStatus())
                .jjType(drainage.getJjType())
                .notes(drainage.getNotes())
                .overdue(overdue)
                .preReminderSentAt(drainage.getPreReminderSentAt())
                .dayOfReminderSentAt(drainage.getDayOfReminderSentAt())
                .createdAt(drainage.getCreatedAt())
                .updatedAt(drainage.getUpdatedAt())
                .build();
    }
}
