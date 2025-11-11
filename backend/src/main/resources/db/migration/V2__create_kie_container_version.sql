-- Create kie_container_versions table to track KieContainer versions and changes

CREATE TABLE IF NOT EXISTS kie_container_versions (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL UNIQUE,
    rules_count INTEGER NOT NULL,
    rules_hash VARCHAR(64) NOT NULL,
    release_id VARCHAR(255),
    changes_description TEXT,
    rule_ids TEXT, -- Comma-separated list of rule IDs in this version
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_date TIMESTAMP,
    last_modified_by VARCHAR(255)
);

COMMENT ON TABLE kie_container_versions IS 'Tracks KieContainer versions and changes on each deployment';
COMMENT ON COLUMN kie_container_versions.version IS 'KieContainer version number (auto-increments on each deploy)';
COMMENT ON COLUMN kie_container_versions.rules_count IS 'Number of rules in this container version';
COMMENT ON COLUMN kie_container_versions.rules_hash IS 'MD5 hash of rules to detect changes';
COMMENT ON COLUMN kie_container_versions.release_id IS 'Drools ReleaseId for this container version';
COMMENT ON COLUMN kie_container_versions.changes_description IS 'Description of changes in this version';
COMMENT ON COLUMN kie_container_versions.rule_ids IS 'Comma-separated list of rule IDs included in this version';

-- Index for version lookup
CREATE INDEX IF NOT EXISTS idx_kie_container_versions_version ON kie_container_versions(version);

-- Index for created_date for timeline queries
CREATE INDEX IF NOT EXISTS idx_kie_container_versions_created_at ON kie_container_versions(created_date DESC);

