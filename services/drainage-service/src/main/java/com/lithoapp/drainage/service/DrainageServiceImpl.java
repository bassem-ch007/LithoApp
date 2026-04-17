package com.lithoapp.drainage.service;

import com.lithoapp.drainage.dto.*;
import com.lithoapp.drainage.entity.Drainage;
import com.lithoapp.drainage.enums.DrainageStatus;
import com.lithoapp.drainage.enums.DrainageType;
import com.lithoapp.drainage.exception.DrainageAlreadyRemovedException;
import com.lithoapp.drainage.exception.DrainageNotFoundException;
import com.lithoapp.drainage.exception.DuplicateActiveDrainageException;
import com.lithoapp.drainage.exception.InvalidDrainageStateException;
import com.lithoapp.drainage.mapper.DrainageMapper;
import com.lithoapp.drainage.repository.DrainageRepository;
import com.lithoapp.drainage.repository.DrainageSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DrainageServiceImpl implements DrainageService {

    private final DrainageRepository drainageRepository;
    private final DrainageMapper drainageMapper;

    // ── Create ────────────────────────────────────────────────────────────────

    @Override
    public DrainageResponse createDrainage(CreateDrainageRequest request) {
        validateJjTypeConsistency(request.getDrainageType(), request);

        // Auto-compute plannedRemovalDate when the urologist did not provide one.
        // If a date was explicitly supplied, it is preserved as-is (honoured below).
        if (request.getPlannedRemovalDate() == null) {
            LocalDate computed = DrainageDurationPolicy.computeDefault(
                    request.getDrainageType(), request.getJjType(), request.getPlacedAt());
            if (computed != null) {
                request.setPlannedRemovalDate(computed);
                log.debug("Auto-computed plannedRemovalDate={} for type={} jjType={}",
                        computed, request.getDrainageType(), request.getJjType());
            }
        }

        validatePlacedAtBeforePlannedRemoval(request);
        checkNoDuplicateActiveDrainage(request);

        Drainage drainage = drainageMapper.toEntity(request);
        Drainage saved = drainageRepository.save(drainage);

        log.info("Created drainage [id={}, patient={}, type={}, side={}, plannedRemoval={}]",
                saved.getId(), saved.getPatientId(), saved.getDrainageType(),
                saved.getSide(), saved.getPlannedRemovalDate());

        return drainageMapper.toResponse(saved);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Override
    public DrainageResponse updateDrainage(UUID id, UpdateDrainageRequest request) {
        Drainage drainage = findById(id);
        requireActive(drainage, "update");

        if (request.getPlannedRemovalDate() != null) {
            if (request.getPlannedRemovalDate().isBefore(drainage.getPlacedAt())) {
                throw new InvalidDrainageStateException(
                    "plannedRemovalDate (" + request.getPlannedRemovalDate() +
                    ") must not be before placedAt (" + drainage.getPlacedAt() + ")."
                );
            }
            drainage.setPlannedRemovalDate(request.getPlannedRemovalDate());
        }

        if (request.getNotes() != null) {
            drainage.setNotes(request.getNotes());
        }

        Drainage saved = drainageRepository.save(drainage);
        log.info("Updated drainage [id={}]", saved.getId());
        return drainageMapper.toResponse(saved);
    }

    // ── Remove ────────────────────────────────────────────────────────────────

    @Override
    public DrainageResponse removeDrainage(UUID id, RemoveDrainageRequest request) {
        Drainage drainage = findById(id);

        if (drainage.getStatus() == DrainageStatus.REMOVED) {
            throw new DrainageAlreadyRemovedException(id);
        }

        if (request.getRemovedAt().isBefore(drainage.getPlacedAt())) {
            throw new InvalidDrainageStateException(
                "removedAt (" + request.getRemovedAt() +
                ") must not be before placedAt (" + drainage.getPlacedAt() + ")."
            );
        }

        drainage.setRemovedAt(request.getRemovedAt());
        drainage.setStatus(DrainageStatus.REMOVED);

        Drainage saved = drainageRepository.save(drainage);
        log.info("Removed drainage [id={}] on {}", saved.getId(), saved.getRemovedAt());
        return drainageMapper.toResponse(saved);
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public DrainageResponse getDrainageById(UUID id) {
        return drainageMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DrainageResponse> getDrainages(DrainageFilterRequest filter) {
        return drainageRepository
                .findAll(DrainageSpecification.fromFilter(filter))
                .stream()
                .map(drainageMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Drainage findById(UUID id) {
        return drainageRepository.findById(id)
                .orElseThrow(() -> new DrainageNotFoundException(id));
    }

    private void requireActive(Drainage drainage, String operation) {
        if (drainage.getStatus() != DrainageStatus.ACTIVE) {
            throw new InvalidDrainageStateException(
                "Cannot " + operation + " drainage [id=" + drainage.getId() +
                "]: it is not ACTIVE (current status: " + drainage.getStatus() + ")."
            );
        }
    }

    /**
     * Rule: jjType must be non-null iff drainageType == JJ.
     */
    private void validateJjTypeConsistency(DrainageType type, CreateDrainageRequest request) {
        boolean isJJ = type == DrainageType.JJ;
        boolean hasJjType = request.getJjType() != null;

        if (isJJ && !hasJjType) {
            throw new InvalidDrainageStateException(
                "jjType is required when drainageType is JJ."
            );
        }
        if (!isJJ && hasJjType) {
            throw new InvalidDrainageStateException(
                "jjType must be null when drainageType is not JJ (got: " + type + ")."
            );
        }
    }

    /**
     * Rule: plannedRemovalDate, if provided, must be >= placedAt.
     */
    private void validatePlacedAtBeforePlannedRemoval(CreateDrainageRequest request) {
        if (request.getPlannedRemovalDate() != null &&
            request.getPlannedRemovalDate().isBefore(request.getPlacedAt())) {
            throw new InvalidDrainageStateException(
                "plannedRemovalDate (" + request.getPlannedRemovalDate() +
                ") must not be before placedAt (" + request.getPlacedAt() + ")."
            );
        }
    }

    /**
     * Rule: no duplicate ACTIVE drainage for the same patient / type / side.
     */
    private void checkNoDuplicateActiveDrainage(CreateDrainageRequest request) {
        boolean exists = drainageRepository.existsByPatientIdAndDrainageTypeAndSideAndStatus(
                request.getPatientId(),
                request.getDrainageType(),
                request.getSide(),
                DrainageStatus.ACTIVE
        );
        if (exists) {
            throw new DuplicateActiveDrainageException(
                    request.getPatientId(), request.getDrainageType(), request.getSide()
            );
        }
    }
}
