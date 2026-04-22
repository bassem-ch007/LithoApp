package com.lithoapp.episodeservice.repository;

import com.lithoapp.episodeservice.entity.Episode;
import com.lithoapp.episodeservice.enums.EpisodeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long>, JpaSpecificationExecutor<Episode> {

    /** All episodes for a patient — used when no status filter is applied. */
    Page<Episode> findByPatientId(Long patientId, Pageable pageable);

    /** Episodes for a patient filtered by status — used for ACTIVE or CLOSED tabs in the frontend. */
    Page<Episode> findByPatientIdAndStatus(Long patientId, EpisodeStatus status, Pageable pageable);

    /** Used to display episode count on the patient profile screen. */
    long countByPatientId(Long patientId);
}
