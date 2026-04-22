package com.lithoapp.episodeservice.enums;

/**
 * Organizational status of a clinical episode (case folder).
 *
 * This is NOT a treatment workflow state — it simply indicates whether
 * the case is still open or has been resolved. Treatment progression,
 * stone analysis results, and drainage lifecycle are managed by their
 * respective dedicated services.
 *
 * ACTIVE : The stone event is ongoing. Analysis and/or drainage records
 *          may be in progress in their own services.
 * CLOSED : The case is resolved. Used for filtering historical episodes.
 *          No hard enforcement — the urologist decides when to close.
 */
public enum EpisodeStatus {
    ACTIVE,
    CLOSED
}
