-- =============================================================================
-- LithoApp — Episode Service Database Init (revised)
-- Target:   PostgreSQL 15+
-- Database: episode_db  (port 5437)
-- =============================================================================
-- HOW TO USE IN DBEAVER:
--   1. Connect to PostgreSQL as superuser (postgres / port 5437)
--   2. The Docker container creates the database automatically.
--      If running manually, uncomment and run the CREATE DATABASE block first.
--   3. Switch active database to episode_db.
--   4. Run this script from Step 1 onwards.
-- =============================================================================


-- =============================================================================
-- STEP 0 — Create database
-- Skip if using Docker Compose — the container creates it automatically.
-- =============================================================================
-- CREATE DATABASE episode_db
--     WITH OWNER = postgres ENCODING = 'UTF8';


-- =============================================================================
-- STEP 1 — Drop existing table if you need a clean reset
-- WARNING: destroys all data. Comment out in production.
-- =============================================================================
-- DROP TABLE IF EXISTS episodes;


-- =============================================================================
-- STEP 2 — Create episodes table
--
-- Design note:
--   Episode is a lightweight case-folder anchoring one stone event to one patient.
--   It does NOT store analysis results, stone anatomy, or treatment workflow details.
--   Those concerns belong to analysis-service and drainage-service respectively.
--   Those services reference this table only via the episodeId foreign key value —
--   there is no actual DB-level FK because patient and episode live in separate
--   service databases.
-- =============================================================================

CREATE TABLE IF NOT EXISTS episodes (

    id            BIGSERIAL     PRIMARY KEY,

    -- Cross-service reference to patient-service.
    -- No FK constraint — integrity enforced at application layer via PatientValidationService.
    patient_id    BIGINT        NOT NULL,

    -- Organizational status: is the case ongoing (ACTIVE) or resolved (CLOSED)?
    -- This is NOT a treatment workflow state. Treatment details live in other services.
    status        VARCHAR(10)   NOT NULL  DEFAULT 'ACTIVE',

    -- Date the stone event was first identified / patient first presented.
    opened_at     DATE          NOT NULL,

    -- Optional short label for this case folder.
    -- Helps the urologist identify the episode in a list.
    -- Example: "2024 Left Ureteral Stone", "Recurrence — Right Kidney"
    title         VARCHAR(255),

    -- Light free-text overarching case summary.
    -- NOT analysis results. NOT treatment notes. Those belong to their services.
    notes         TEXT,

    -- True if the urologist flags this as a stone recurrence for this patient.
    is_recurrence BOOLEAN       NOT NULL  DEFAULT FALSE,

    created_at    TIMESTAMP     NOT NULL  DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NOT NULL  DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_episode_status CHECK (status IN ('ACTIVE', 'CLOSED'))
);

COMMENT ON TABLE  episodes               IS 'Case folders — one per stone event per patient. Anchor for analysis and drainage records.';
COMMENT ON COLUMN episodes.patient_id    IS 'Cross-service reference to patient-service. No DB-level FK.';
COMMENT ON COLUMN episodes.status        IS 'Organizational status: ACTIVE (ongoing) or CLOSED (resolved).';
COMMENT ON COLUMN episodes.title         IS 'Optional short case label for display in patient episode timeline.';
COMMENT ON COLUMN episodes.notes         IS 'Overarching case summary — not a substitute for analysis or drainage notes.';
COMMENT ON COLUMN episodes.is_recurrence IS 'True if this is a recurring stone event for this patient.';


-- =============================================================================
-- STEP 3 — Indexes
-- =============================================================================

-- Primary query: all episodes for a patient (patient timeline)
CREATE INDEX IF NOT EXISTS idx_episode_patient_id
    ON episodes (patient_id);

-- Most common filtered query: active episodes for a patient
CREATE INDEX IF NOT EXISTS idx_episode_patient_status
    ON episodes (patient_id, status);

-- Default sort: most recently opened episodes first
CREATE INDEX IF NOT EXISTS idx_episode_opened_at
    ON episodes (opened_at DESC);


-- =============================================================================
-- STEP 4 — Auto-update updated_at via trigger
-- Hibernate @UpdateTimestamp handles this in the JPA layer.
-- This trigger also covers direct SQL updates from DBeaver or admin scripts.
-- =============================================================================

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_episodes_updated_at ON episodes;

CREATE TRIGGER trg_episodes_updated_at
    BEFORE UPDATE ON episodes
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();


-- =============================================================================
-- STEP 5 — Sample data (development / testing only)
-- Remove this block before running in production.
-- patient_id values 1, 2, 3 must exist in patient-service.
-- =============================================================================

INSERT INTO episodes (patient_id, status, opened_at, title, notes, is_recurrence)
VALUES
    (
        1,
        'ACTIVE',
        '2024-03-15',
        '2024 — Left Ureteral Stone',
        'Patient presented with acute left flank pain. Stone event confirmed by CT. Case opened for follow-up and analysis.',
        FALSE
    ),
    (
        1,
        'CLOSED',
        '2023-06-10',
        '2023 — Right Renal Stone',
        'First stone event for this patient. Case resolved after conservative management.',
        FALSE
    ),
    (
        2,
        'ACTIVE',
        '2024-02-20',
        'Recurrence — Bilateral Stones 2024',
        'Second stone episode for this patient. Bilateral involvement noted. Metabolic workup requested via analysis-service.',
        TRUE
    ),
    (
        3,
        'CLOSED',
        '2024-01-08',
        '2024 — Right Flank Discomfort',
        'Case opened after patient reported chronic right flank discomfort. Resolved.',
        FALSE
    )
ON CONFLICT DO NOTHING;


-- =============================================================================
-- Verification queries
-- =============================================================================

-- SELECT id, patient_id, status, opened_at, title, is_recurrence
-- FROM   episodes
-- ORDER  BY patient_id, opened_at DESC;

-- SELECT patient_id, status, COUNT(*) as total
-- FROM   episodes
-- GROUP  BY patient_id, status
-- ORDER  BY patient_id;
