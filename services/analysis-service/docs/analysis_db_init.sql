-- =============================================================================
-- LithoApp — Analysis Service Database Init
-- Target:   PostgreSQL 15+
-- Database: bilan_db  (port 5433)
-- =============================================================================
-- Design note:
--   Every analysis request is anchored to an episode (episode_id), not just a
--   patient. This reflects the workflow: patient → episode → analysis.
--   patient_id is kept for filtering and reporting, but episode_id is the
--   primary case reference.
--   No DB-level FKs to episode_db or patient_db — cross-service integrity is
--   enforced at the application layer:
--     • PatientValidationService  → calls patient-service (HTTP) to verify patient exists + active
--     • EpisodeValidationService  → stub (→ future Feign) to verify episode exists + belongs to patient
-- =============================================================================


-- =============================================================================
-- STEP 1 — Drop and recreate (clean reset for dev)
-- Comment out in production.
-- =============================================================================
-- DROP TABLE IF EXISTS audit_entries CASCADE;
-- DROP TABLE IF EXISTS pdf_documents CASCADE;
-- DROP TABLE IF EXISTS metabolic_results CASCADE;
-- DROP TABLE IF EXISTS stone_results CASCADE;
-- DROP TABLE IF EXISTS analysis_requests CASCADE;


-- =============================================================================
-- STEP 2 — analysis_requests
-- =============================================================================

CREATE TABLE IF NOT EXISTS analysis_requests (
    id              BIGSERIAL       PRIMARY KEY,

    -- Primary case anchor: which episode does this analysis belong to?
    -- Cross-service reference to episode-service. No DB-level FK.
    episode_id      BIGINT          NOT NULL,

    -- Patient reference — kept for filtering and reporting.
    -- Must match the patient of the referenced episode.
    patient_id      BIGINT          NOT NULL,

    created_by      VARCHAR(255)    NOT NULL,

    type            VARCHAR(20)     NOT NULL,  -- METABOLIC | STONE
    status          VARCHAR(20)     NOT NULL DEFAULT 'CREATED',

    created_at      TIMESTAMP       NOT NULL,
    completed_at    TIMESTAMP,
    completed_by    VARCHAR(255),

    version         BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT chk_analysis_type
        CHECK (type IN ('METABOLIC', 'STONE')),

    CONSTRAINT chk_analysis_status
        CHECK (status IN ('CREATED', 'IN_PROGRESS', 'COMPLETED'))
);

COMMENT ON TABLE  analysis_requests              IS 'Analysis bilan requests. Each request belongs to one episode (case folder).';
COMMENT ON COLUMN analysis_requests.episode_id   IS 'Primary case anchor — cross-service reference to episode-service.';
COMMENT ON COLUMN analysis_requests.patient_id   IS 'Kept for filtering and reporting. Must match episode.patient_id.';
COMMENT ON COLUMN analysis_requests.type         IS 'METABOLIC: 3-PDF metabolic bilan. STONE: morphology + spectrophotometry + classification.';
COMMENT ON COLUMN analysis_requests.status       IS 'CREATED: no contributions yet. IN_PROGRESS: at least one result contribution. COMPLETED: immutable.';
COMMENT ON COLUMN analysis_requests.version      IS 'Optimistic locking version counter.';


-- =============================================================================
-- STEP 3 — metabolic_results
-- =============================================================================

CREATE TABLE IF NOT EXISTS metabolic_results (
    id                      BIGSERIAL   PRIMARY KEY,
    analysis_request_id     BIGINT      NOT NULL UNIQUE,
    created_at              TIMESTAMP   NOT NULL,

    CONSTRAINT fk_metabolic_request
        FOREIGN KEY (analysis_request_id)
        REFERENCES analysis_requests(id)
        ON DELETE CASCADE
);

COMMENT ON TABLE metabolic_results IS 'Container for the three metabolic PDF documents (one per MetabolicDocumentType). Created automatically with METABOLIC requests.';


-- =============================================================================
-- STEP 4 — pdf_documents (versioned uploads for metabolic results)
-- =============================================================================

CREATE TABLE IF NOT EXISTS pdf_documents (
    id                      BIGSERIAL       PRIMARY KEY,
    metabolic_result_id     BIGINT          NOT NULL,
    document_type           VARCHAR(30)     NOT NULL,   -- BLOOD_TEST | MORNING_URINE | H24_URINE
    version_number          INT             NOT NULL DEFAULT 1,
    is_active               BOOLEAN         NOT NULL DEFAULT TRUE,
    storage_key             VARCHAR(500)    NOT NULL,
    original_filename       VARCHAR(255)    NOT NULL,
    file_size_bytes         BIGINT          NOT NULL,
    uploaded_by             VARCHAR(255)    NOT NULL,
    uploaded_at             TIMESTAMP       NOT NULL,

    CONSTRAINT fk_pdf_metabolic
        FOREIGN KEY (metabolic_result_id)
        REFERENCES metabolic_results(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_pdf_document_type
        CHECK (document_type IN ('BLOOD_TEST', 'MORNING_URINE', 'H24_URINE'))
);

COMMENT ON TABLE  pdf_documents                IS 'Versioned PDF uploads for a metabolic result. Multiple versions may exist per document type; only one is active at a time.';
COMMENT ON COLUMN pdf_documents.is_active      IS 'TRUE for the latest upload of a given document_type. Previous uploads remain in storage but are marked FALSE.';
COMMENT ON COLUMN pdf_documents.version_number IS 'Monotonically increasing version counter per (metabolic_result_id, document_type).';
COMMENT ON COLUMN pdf_documents.storage_key    IS 'Opaque key used to retrieve the file from the configured storage adapter (MinIO or local).';


-- =============================================================================
-- STEP 5 — stone_results
-- =============================================================================

CREATE TABLE IF NOT EXISTS stone_results (
    id                      BIGSERIAL       PRIMARY KEY,
    analysis_request_id     BIGINT          NOT NULL UNIQUE,

    -- Morphological analysis
    morph_size              VARCHAR(255),
    morph_surface           VARCHAR(255),
    morph_color             VARCHAR(255),
    morph_section           VARCHAR(255),
    morph_outer_layers      VARCHAR(255),
    morph_core              VARCHAR(255),

    -- Infrared spectrophotometry
    spectro_surface         VARCHAR(255),
    spectro_section         VARCHAR(255),
    spectro_outer_layers    VARCHAR(255),
    spectro_core            VARCHAR(255),

    -- Final classification (required before COMPLETED)
    final_stone_type        VARCHAR(255),

    -- Provenance
    last_modified_by        VARCHAR(255),
    last_modified_at        TIMESTAMP,

    version                 BIGINT          NOT NULL DEFAULT 0,
    created_at              TIMESTAMP       NOT NULL,

    CONSTRAINT fk_stone_request
        FOREIGN KEY (analysis_request_id)
        REFERENCES analysis_requests(id)
        ON DELETE CASCADE
);

COMMENT ON TABLE  stone_results                IS 'Structured stone morphology and spectrophotometry result. Created (empty) with STONE requests.';
COMMENT ON COLUMN stone_results.final_stone_type IS 'Required before the parent analysis_request can be marked COMPLETED.';
COMMENT ON COLUMN stone_results.version          IS 'Optimistic locking — protects against concurrent biologist edits (HTTP 409 on conflict).';


-- =============================================================================
-- STEP 6 — audit_entries
-- =============================================================================

CREATE TABLE IF NOT EXISTS audit_entries (
    id                      BIGSERIAL       PRIMARY KEY,
    analysis_request_id     BIGINT          NOT NULL,
    actor_id                VARCHAR(255)    NOT NULL,
    action_type             VARCHAR(60)     NOT NULL,
    field_name              VARCHAR(255),
    old_value               TEXT,
    new_value               TEXT,
    occurred_at             TIMESTAMP       NOT NULL,

    CONSTRAINT fk_audit_request
        FOREIGN KEY (analysis_request_id)
        REFERENCES analysis_requests(id)
        ON DELETE CASCADE
);

COMMENT ON TABLE audit_entries IS 'Immutable audit log. One row per significant event on an analysis request.';


-- =============================================================================
-- STEP 7 — Indexes
-- =============================================================================

-- Primary query: all analysis requests for an episode (episode detail screen)
CREATE INDEX IF NOT EXISTS idx_ar_episode_id
    ON analysis_requests (episode_id);

-- Secondary query: all analysis requests for a patient across all episodes
CREATE INDEX IF NOT EXISTS idx_ar_patient_id
    ON analysis_requests (patient_id);

-- Composite: episode + status
CREATE INDEX IF NOT EXISTS idx_ar_episode_status
    ON analysis_requests (episode_id, status);

-- Composite: patient + status
CREATE INDEX IF NOT EXISTS idx_ar_patient_status
    ON analysis_requests (patient_id, status);

-- Status-only (lab/admin view)
CREATE INDEX IF NOT EXISTS idx_ar_status
    ON analysis_requests (status);

-- Audit log lookup
CREATE INDEX IF NOT EXISTS idx_audit_request_id
    ON audit_entries (analysis_request_id);

CREATE INDEX IF NOT EXISTS idx_audit_occurred_at
    ON audit_entries (occurred_at DESC);

-- PDF lookup per metabolic result
CREATE INDEX IF NOT EXISTS idx_pdf_metabolic_result_id
    ON pdf_documents (metabolic_result_id);

CREATE INDEX IF NOT EXISTS idx_pdf_active_type
    ON pdf_documents (metabolic_result_id, document_type, is_active);


-- =============================================================================
-- STEP 8 — Sample data (development / testing only)
-- episode_id values must exist in episode-service (episode_db).
-- patient_id values must match the patient of those episodes.
-- =============================================================================

INSERT INTO analysis_requests (episode_id, patient_id, created_by, type, status, created_at, version)
VALUES
    (1, 1, 'dr.smith', 'METABOLIC', 'COMPLETED', '2024-03-16 09:00:00', 0),
    (1, 1, 'dr.smith', 'STONE',     'IN_PROGRESS', '2024-03-20 10:30:00', 0),
    (2, 1, 'dr.jones', 'METABOLIC', 'CREATED',   '2024-04-01 08:15:00', 0),
    (3, 2, 'dr.smith', 'STONE',     'COMPLETED', '2024-03-01 11:00:00', 0)
ON CONFLICT DO NOTHING;

-- Mark completed requests
UPDATE analysis_requests
SET completed_at = '2024-03-30 14:00:00', completed_by = 'bio.lab1'
WHERE episode_id = 1 AND type = 'METABOLIC' AND status = 'COMPLETED';

UPDATE analysis_requests
SET completed_at = '2024-03-20 16:00:00', completed_by = 'bio.lab2'
WHERE episode_id = 3 AND type = 'STONE' AND status = 'COMPLETED';


-- =============================================================================
-- Verification queries
-- =============================================================================

-- All analysis requests grouped by episode
-- SELECT episode_id, patient_id, type, status, created_at, completed_at
-- FROM   analysis_requests
-- ORDER  BY episode_id, created_at;

-- Active (non-completed) requests
-- SELECT id, episode_id, patient_id, type, status, created_at
-- FROM   analysis_requests
-- WHERE  status <> 'COMPLETED'
-- ORDER  BY created_at;
