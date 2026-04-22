-- =============================================================================
-- LithoApp — Drainage Service Database Init (revised)
-- Target:   PostgreSQL 15+
-- Database: drainage_db  (port 5434)
-- =============================================================================
-- Design note:
--   Every drainage record is anchored to an episode (episode_id), not just a
--   patient. This reflects the workflow: patient → episode → drainage.
--   patient_id is kept for filtering, reporting, and scheduler log context,
--   but episode_id is the primary case reference.
--   No DB-level FKs to episode_db or patient_db — cross-service integrity is
--   enforced at the application layer:
--     • PatientValidationService  → calls patient-service (HTTP) to verify patient exists + active
--     • EpisodeValidationService  → stub (→ future Feign) to verify episode exists
-- =============================================================================


-- =============================================================================
-- STEP 1 — Drop and recreate (clean reset for dev)
-- Comment out in production.
-- =============================================================================
-- DROP TABLE IF EXISTS drainages;


-- =============================================================================
-- STEP 2 — Create drainages table
-- =============================================================================

CREATE TABLE IF NOT EXISTS drainages (

    id                      UUID            PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Primary case anchor: which episode does this drainage belong to?
    -- Cross-service reference to episode-service. No DB-level FK.
    episode_id              BIGINT          NOT NULL,

    -- Patient reference — kept for filtering and scheduler log context.
    -- Must match the patient of the referenced episode.
    patient_id              BIGINT          NOT NULL,

    -- Treating doctor reference — cross-service, no FK.
    doctor_id               BIGINT          NOT NULL,

    -- Drainage device classification
    drainage_type           VARCHAR(20)     NOT NULL,   -- JJ | URETERAL | NEPHROSTOMY
    side                    VARCHAR(10)     NOT NULL,   -- LEFT | RIGHT | BILATERAL
    jj_type                 VARCHAR(20),                -- STANDARD_6F | LARGE_7F | BIODEGRADABLE | METALLIC (JJ only)

    -- Lifecycle dates
    placed_at               DATE            NOT NULL,
    planned_removal_date    DATE,
    removed_at              DATE,

    -- Operational status
    status                  VARCHAR(10)     NOT NULL    DEFAULT 'ACTIVE',  -- ACTIVE | REMOVED

    -- Free-text notes
    notes                   TEXT,

    -- Notification tracking (populated by the scheduler)
    pre_reminder_sent_at    TIMESTAMP,
    day_of_reminder_sent_at TIMESTAMP,

    -- Audit
    created_at              TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,

    -- -------------------------------------------------------------------------
    -- Check constraints
    -- -------------------------------------------------------------------------
    CONSTRAINT chk_drainage_type
        CHECK (drainage_type IN ('JJ', 'URETERAL', 'NEPHROSTOMY')),

    CONSTRAINT chk_drainage_side
        CHECK (side IN ('LEFT', 'RIGHT', 'BILATERAL')),

    CONSTRAINT chk_jj_type
        CHECK (jj_type IN ('STANDARD_6F', 'LARGE_7F', 'BIODEGRADABLE', 'METALLIC') OR jj_type IS NULL),

    CONSTRAINT chk_drainage_status
        CHECK (status IN ('ACTIVE', 'REMOVED')),

    -- JJ stents must have a jj_type; other types must not
    CONSTRAINT chk_jj_type_consistency
        CHECK (
            (drainage_type = 'JJ' AND jj_type IS NOT NULL)
            OR
            (drainage_type <> 'JJ' AND jj_type IS NULL)
        ),

    CONSTRAINT chk_removal_after_placement
        CHECK (removed_at IS NULL OR removed_at >= placed_at),

    CONSTRAINT chk_planned_removal_after_placement
        CHECK (planned_removal_date IS NULL OR planned_removal_date >= placed_at),

    -- A REMOVED drainage must have a removed_at date
    CONSTRAINT chk_removed_status_has_date
        CHECK (status <> 'REMOVED' OR removed_at IS NOT NULL)
);

COMMENT ON TABLE  drainages              IS 'JJ stent and drainage records. Each record belongs to one episode (case folder).';
COMMENT ON COLUMN drainages.episode_id   IS 'Primary case anchor — cross-service reference to episode-service.';
COMMENT ON COLUMN drainages.patient_id   IS 'Kept for filtering and reporting. Must match episode.patient_id.';
COMMENT ON COLUMN drainages.jj_type      IS 'JJ sub-type. Required when drainage_type=JJ, must be NULL otherwise.';
COMMENT ON COLUMN drainages.status       IS 'ACTIVE: device in place. REMOVED: device physically removed.';


-- =============================================================================
-- STEP 3 — Indexes
-- =============================================================================

-- Primary query: all drainages for an episode (episode detail screen)
CREATE INDEX IF NOT EXISTS idx_drainage_episode_id
    ON drainages (episode_id);

-- Secondary query: all drainages for a patient across all episodes
CREATE INDEX IF NOT EXISTS idx_drainage_patient_id
    ON drainages (patient_id);

-- Composite: episode + status (active drainages for an episode)
CREATE INDEX IF NOT EXISTS idx_drainage_episode_status
    ON drainages (episode_id, status);

-- Scheduler queries: active drainages by planned removal date
CREATE INDEX IF NOT EXISTS idx_drainage_status
    ON drainages (status);

CREATE INDEX IF NOT EXISTS idx_drainage_planned_removal
    ON drainages (planned_removal_date)
    WHERE status = 'ACTIVE';


-- =============================================================================
-- STEP 4 — Auto-update updated_at trigger
-- =============================================================================

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_drainages_updated_at ON drainages;

CREATE TRIGGER trg_drainages_updated_at
    BEFORE UPDATE ON drainages
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();


-- =============================================================================
-- STEP 5 — Sample data (development / testing only)
-- episode_id values must exist in episode-service (episode_db).
-- patient_id values must match the patient of those episodes.
-- =============================================================================

INSERT INTO drainages (episode_id, patient_id, doctor_id, drainage_type, side, jj_type, placed_at, planned_removal_date, status, notes)
VALUES
    (
        -- Episode 1: patient 1, active stone case
        1, 1, 10,
        'JJ', 'LEFT', 'STANDARD_6F',
        '2024-03-16',
        '2024-06-14',   -- 90 days default for STANDARD_6F
        'ACTIVE',
        'JJ stent placed post-ureteroscopy. Left proximal ureteral stone. Follow-up in 3 months.'
    ),
    (
        -- Episode 3: patient 2, active bilateral case
        3, 2, 10,
        'JJ', 'RIGHT', 'LARGE_7F',
        '2024-03-01',
        '2024-05-30',
        'ACTIVE',
        'Large bore JJ placed right side after ESWL session 1. Bilateral case — left side managed conservatively.'
    ),
    (
        -- Episode 2: patient 1, closed case — drainage already removed
        2, 1, 11,
        'URETERAL', 'RIGHT', NULL,
        '2023-06-12',
        '2023-06-26',
        'REMOVED',
        'Short-term ureteral catheter post-procedure. Removed as planned.'
    ),
    (
        -- Episode 4: patient 2, archived case
        4, 2, 11,
        'JJ', 'RIGHT', 'STANDARD_6F',
        '2021-09-16',
        '2021-12-15',
        'REMOVED',
        'JJ stent placed post-URSL for right proximal stone. Removed on schedule. Patient stone-free.'
    )
ON CONFLICT DO NOTHING;

-- Mark removed drainages with actual removal dates
UPDATE drainages
SET removed_at = '2023-06-26'
WHERE episode_id = 2 AND status = 'REMOVED';

UPDATE drainages
SET removed_at = '2021-12-14'
WHERE episode_id = 4 AND status = 'REMOVED';


-- =============================================================================
-- Verification queries
-- =============================================================================

-- All drainages grouped by episode
-- SELECT episode_id, patient_id, drainage_type, side, jj_type, status, placed_at, planned_removal_date
-- FROM   drainages
-- ORDER  BY episode_id, placed_at;

-- Active drainages overdue today
-- SELECT id, episode_id, patient_id, drainage_type, side, planned_removal_date
-- FROM   drainages
-- WHERE  status = 'ACTIVE'
--   AND  planned_removal_date < CURRENT_DATE;
