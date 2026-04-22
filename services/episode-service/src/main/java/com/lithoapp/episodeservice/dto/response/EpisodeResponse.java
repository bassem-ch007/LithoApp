package com.lithoapp.episodeservice.dto.response;

import com.lithoapp.episodeservice.enums.EpisodeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Full episode detail response — returned by create and get-by-id. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeResponse {

    private Long id;
    private Long patientId;
    private EpisodeStatus status;
    private LocalDate openedAt;
    private String title;
    private String notes;
    private boolean recurrence;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
