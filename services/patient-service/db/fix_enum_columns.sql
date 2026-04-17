-- =============================================================================
-- LithoApp – Patient Service
-- One-time fix: convert custom PG enum columns to VARCHAR
-- Run this ONCE in DBeaver against lithoapp_patients if you already ran init.sql
-- =============================================================================

-- 1. Convert patients.gender from gender_enum → VARCHAR(10)
ALTER TABLE patients
    ALTER COLUMN gender TYPE VARCHAR(10) USING gender::TEXT;

-- 2. Convert clinical_info.kidney_type from kidney_type_enum → VARCHAR(20)
ALTER TABLE clinical_info
    ALTER COLUMN kidney_type TYPE VARCHAR(20) USING kidney_type::TEXT;

-- 3. Drop the now-unused custom enum types (optional but clean)
DROP TYPE IF EXISTS gender_enum;
DROP TYPE IF EXISTS kidney_type_enum;
