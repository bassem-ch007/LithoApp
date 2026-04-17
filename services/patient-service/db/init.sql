-- =============================================================================
-- LithoApp – Patient Service
-- PostgreSQL DDL Script
-- Run this in DBeaver against the target database: lithoapp_patients
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 0. Create database (run this separately as superuser if needed)
-- -----------------------------------------------------------------------------
-- CREATE DATABASE lithoapp_patients
--     WITH OWNER = postgres
--          ENCODING = 'UTF8'
--          LC_COLLATE = 'en_US.UTF-8'
--          LC_CTYPE   = 'en_US.UTF-8'
--          TEMPLATE = template0;

-- \c lithoapp_patients   -- connect to the new database before running the rest


-- =============================================================================
-- 1. SEQUENCES  (one per table — mirrors JPA IDENTITY strategy)
-- =============================================================================

CREATE SEQUENCE IF NOT EXISTS patients_id_seq             START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS clinical_info_id_seq        START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS medications_id_seq          START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS associated_diseases_id_seq  START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS genetic_diseases_id_seq     START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS anatomical_anomalies_id_seq START 1 INCREMENT 1;


-- =============================================================================
-- 3. TABLES
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 3.1  patients  (core identity & demographics)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS patients (
    id          BIGINT          NOT NULL DEFAULT nextval('patients_id_seq'),
    di          VARCHAR(50)     NOT NULL,
    dmi         VARCHAR(50)     NOT NULL,
    first_name  VARCHAR(100)    NOT NULL,
    last_name   VARCHAR(100)    NOT NULL,
    birth_date  DATE            NOT NULL,
    gender      VARCHAR(10)     NOT NULL,
    height      DOUBLE PRECISION,
    weight      DOUBLE PRECISION,
    address     VARCHAR(255),
    email       VARCHAR(150),
    phone       VARCHAR(20),
    active      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_patients       PRIMARY KEY (id),
    CONSTRAINT uq_patients_di    UNIQUE (di),
    CONSTRAINT uq_patients_dmi   UNIQUE (dmi),
    CONSTRAINT chk_height_pos    CHECK (height  IS NULL OR height  > 0),
    CONSTRAINT chk_weight_pos    CHECK (weight  IS NULL OR weight  > 0),
    CONSTRAINT chk_email_format  CHECK (email   IS NULL OR email ~* '^[^@\s]+@[^@\s]+\.[^@\s]+$'),
    CONSTRAINT chk_birth_past    CHECK (birth_date < CURRENT_DATE)
);

COMMENT ON TABLE  patients            IS 'Core patient identity and demographic data';
COMMENT ON COLUMN patients.di         IS 'Dossier d''Identité – unique patient identifier across the clinic';
COMMENT ON COLUMN patients.dmi        IS 'Dossier Médical Informatisé – unique medical record number';
COMMENT ON COLUMN patients.active     IS 'Soft-delete flag; FALSE = deactivated patient';


-- -----------------------------------------------------------------------------
-- 3.2  clinical_info  (Facteurs généraux — one row per patient)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS clinical_info (
    id                    BIGINT           NOT NULL DEFAULT nextval('clinical_info_id_seq'),
    patient_id            BIGINT           NOT NULL,
    family_history        BOOLEAN,
    personal_history      BOOLEAN,
    last_episode_date     DATE,
    lithiasis_type        VARCHAR(100),
    frequent_infections   BOOLEAN,
    single_kidney         BOOLEAN,
    kidney_type           VARCHAR(20),
    chronic_renal_failure BOOLEAN,
    clearance             DOUBLE PRECISION,

    CONSTRAINT pk_clinical_info            PRIMARY KEY (id),
    CONSTRAINT uq_clinical_info_patient    UNIQUE (patient_id),
    CONSTRAINT fk_clinical_info_patient    FOREIGN KEY (patient_id)
                                               REFERENCES patients (id)
                                               ON DELETE CASCADE,
    CONSTRAINT chk_clearance_pos           CHECK (clearance IS NULL OR clearance > 0)
);

COMMENT ON TABLE  clinical_info               IS 'General risk factors (Facteurs généraux) — structured clinical data';
COMMENT ON COLUMN clinical_info.lithiasis_type IS 'Stone composition type (e.g. oxalate de calcium, urate)';
COMMENT ON COLUMN clinical_info.clearance      IS 'Creatinine clearance in mL/min (GFR)';


-- -----------------------------------------------------------------------------
-- 3.3  medications  (Traitement — YES/NO + description; one row per patient)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS medications (
    id              BIGINT      NOT NULL DEFAULT nextval('medications_id_seq'),
    patient_id      BIGINT      NOT NULL,
    has_medication  BOOLEAN     NOT NULL,
    description     TEXT,

    CONSTRAINT pk_medications           PRIMARY KEY (id),
    CONSTRAINT uq_medications_patient   UNIQUE (patient_id),
    CONSTRAINT fk_medications_patient   FOREIGN KEY (patient_id)
                                            REFERENCES patients (id)
                                            ON DELETE CASCADE
);

COMMENT ON TABLE  medications             IS 'Current medication — presence flag + free-text description';
COMMENT ON COLUMN medications.description IS 'Medication names, dosages, and schedule (free text)';


-- -----------------------------------------------------------------------------
-- 3.4  associated_diseases  (Maladies associées — list per patient)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS associated_diseases (
    id          BIGINT       NOT NULL DEFAULT nextval('associated_diseases_id_seq'),
    patient_id  BIGINT       NOT NULL,
    name        VARCHAR(150) NOT NULL,

    CONSTRAINT pk_associated_diseases         PRIMARY KEY (id),
    CONSTRAINT fk_associated_diseases_patient FOREIGN KEY (patient_id)
                                                  REFERENCES patients (id)
                                                  ON DELETE CASCADE
);

COMMENT ON TABLE associated_diseases IS 'Co-morbidities associated with the patient (e.g. HTA, diabète)';


-- -----------------------------------------------------------------------------
-- 3.5  genetic_diseases  (Maladies génétiques — list per patient)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS genetic_diseases (
    id          BIGINT       NOT NULL DEFAULT nextval('genetic_diseases_id_seq'),
    patient_id  BIGINT       NOT NULL,
    name        VARCHAR(150) NOT NULL,

    CONSTRAINT pk_genetic_diseases         PRIMARY KEY (id),
    CONSTRAINT fk_genetic_diseases_patient FOREIGN KEY (patient_id)
                                               REFERENCES patients (id)
                                               ON DELETE CASCADE
);

COMMENT ON TABLE genetic_diseases IS 'Genetic diseases relevant to kidney stone formation';


-- -----------------------------------------------------------------------------
-- 3.6  anatomical_anomalies  (Anomalies anatomiques — list per patient)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS anatomical_anomalies (
    id          BIGINT       NOT NULL DEFAULT nextval('anatomical_anomalies_id_seq'),
    patient_id  BIGINT       NOT NULL,
    name        VARCHAR(150) NOT NULL,

    CONSTRAINT pk_anatomical_anomalies         PRIMARY KEY (id),
    CONSTRAINT fk_anatomical_anomalies_patient FOREIGN KEY (patient_id)
                                                   REFERENCES patients (id)
                                                   ON DELETE CASCADE
);

COMMENT ON TABLE anatomical_anomalies IS 'Urinary tract structural anomalies (e.g. rein en fer à cheval)';


-- =============================================================================
-- 4. INDEXES  (beyond the implicit UNIQUE / PK indexes)
-- =============================================================================

-- Full-text / LIKE searches on name
CREATE INDEX IF NOT EXISTS idx_patients_last_name   ON patients (last_name);
CREATE INDEX IF NOT EXISTS idx_patients_first_name  ON patients (first_name);

-- Phone search
CREATE INDEX IF NOT EXISTS idx_patients_phone       ON patients (phone);

-- Active flag (filter inactive patients quickly)
CREATE INDEX IF NOT EXISTS idx_patients_active      ON patients (active);

-- FK lookup indexes on child tables
CREATE INDEX IF NOT EXISTS idx_clinical_info_patient       ON clinical_info        (patient_id);
CREATE INDEX IF NOT EXISTS idx_medications_patient         ON medications          (patient_id);
CREATE INDEX IF NOT EXISTS idx_associated_diseases_patient ON associated_diseases  (patient_id);
CREATE INDEX IF NOT EXISTS idx_genetic_diseases_patient    ON genetic_diseases     (patient_id);
CREATE INDEX IF NOT EXISTS idx_anatomical_anomalies_patient ON anatomical_anomalies (patient_id);


-- =============================================================================
-- 5. AUTO-UPDATE updated_at  (trigger)
-- =============================================================================

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_patients_updated_at ON patients;
CREATE TRIGGER trg_patients_updated_at
    BEFORE UPDATE ON patients
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();


-- =============================================================================
-- 6. SAMPLE DATA  (optional — remove before production)
-- =============================================================================

INSERT INTO patients (di, dmi, first_name, last_name, birth_date, gender, height, weight,
                      address, email, phone, active)
VALUES
    ('DI-2024-001', 'DMI-2024-00142', 'Ahmed',   'Benali',  '1978-06-15', 'MALE',   178.0, 82.0,
     '12 Rue des Acacias, Alger',       'ahmed.benali@example.com',   '+213 555 123 456', TRUE),
    ('DI-2024-002', 'DMI-2024-00143', 'Fatima',  'Khelil',  '1985-03-22', 'FEMALE', 162.0, 60.0,
     '5 Avenue Didouche Mourad, Alger', 'fatima.khelil@example.com',  '+213 555 789 012', TRUE),
    ('DI-2024-003', 'DMI-2024-00144', 'Karim',   'Meziani', '1991-11-08', 'MALE',   175.0, 78.5,
     '88 Rue Hassiba Ben Bouali, Oran', 'karim.meziani@example.com',  '+213 555 345 678', TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO clinical_info (patient_id, family_history, personal_history, last_episode_date,
                           lithiasis_type, frequent_infections, single_kidney,
                           kidney_type, chronic_renal_failure, clearance)
VALUES
    (1, TRUE,  TRUE,  '2023-09-10', 'Oxalate de calcium',  FALSE, FALSE, NULL,          TRUE,  45.5),
    (2, FALSE, TRUE,  '2024-01-05', 'Phosphate de calcium', TRUE, FALSE, NULL,          FALSE, 88.0),
    (3, TRUE,  FALSE, NULL,         'Urate',               FALSE, TRUE,  'ANATOMICAL',  FALSE, 72.0)
ON CONFLICT DO NOTHING;

INSERT INTO medications (patient_id, has_medication, description)
VALUES
    (1, TRUE,  'Hydrochlorothiazide 25mg – 1 fois/jour\nAllopurinol 100mg – 1 fois/jour'),
    (2, TRUE,  'Citrate de potassium 10mEq – 2 fois/jour'),
    (3, FALSE, NULL)
ON CONFLICT DO NOTHING;

INSERT INTO associated_diseases (patient_id, name) VALUES
    (1, 'Hypertension artérielle'),
    (1, 'Diabète type 2'),
    (2, 'Hyperparathyroïdie primaire'),
    (3, 'Goutte');

INSERT INTO genetic_diseases (patient_id, name) VALUES
    (1, 'Hyperoxalurie primaire de type 1'),
    (3, 'Syndrome de Lesch-Nyhan');

INSERT INTO anatomical_anomalies (patient_id, name) VALUES
    (1, 'Rein en fer à cheval'),
    (3, 'Syndrome de la jonction pyélo-urétérale');


-- =============================================================================
-- 7. VERIFICATION QUERIES
-- =============================================================================

-- Quick sanity check after running the script:
-- SELECT * FROM patients;
-- SELECT p.di, p.dmi, p.first_name, p.last_name, ci.lithiasis_type, ci.clearance
--   FROM patients p
--   LEFT JOIN clinical_info ci ON ci.patient_id = p.id;
