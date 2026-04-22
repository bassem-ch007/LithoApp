package com.lithoapp.episodeservice.dto.response;

import com.lithoapp.episodeservice.enums.EpisodeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Lightweight projection used in list / patient-timeline endpoints. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeSummaryResponse {

    private Long id;
    private Long patientId;
    private EpisodeStatus status;
    private LocalDate openedAt;
    private String title;
    private boolean recurrence;
    private LocalDateTime createdAt;
}
