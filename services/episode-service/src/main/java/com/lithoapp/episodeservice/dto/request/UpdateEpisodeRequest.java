package com.lithoapp.episodeservice.dto.request;

import com.lithoapp.episodeservice.enums.EpisodeStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Partial update request for episode case-folder fields.
 * All fields are optional — null values are ignored by the mapper.
 *
 * Status (ACTIVE/CLOSED) is intentionally part of this request.
 * Closing a case is not a complex workflow transition — it is simply
 * the urologist marking the case folder as resolved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEpisodeRequest {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;

    /** Use Boolean (not boolean) to distinguish "not provided" from false. */
    private Boolean recurrence;

    /** ACTIVE or CLOSED. Changing to CLOSED marks the case as resolved. */
    private EpisodeStatus status;
}
