-- Add fact_type support to decision_rules and kie_container_versions
-- This allows multiple KieContainers for different fact types (e.g., Declaration, Order, Customer)

-- Add fact_type column to decision_rules
ALTER TABLE decision_rules 
ADD COLUMN IF NOT EXISTS fact_type VARCHAR(100) DEFAULT 'Declaration' NOT NULL;

-- Add index for fact_type
CREATE INDEX IF NOT EXISTS idx_decision_rules_fact_type ON decision_rules(fact_type);

-- Add fact_type column to kie_container_versions
ALTER TABLE kie_container_versions 
ADD COLUMN IF NOT EXISTS fact_type VARCHAR(100) DEFAULT 'Declaration' NOT NULL;

-- Add index for fact_type in kie_container_versions
CREATE INDEX IF NOT EXISTS idx_kie_container_versions_fact_type ON kie_container_versions(fact_type);

-- Add unique constraint for (fact_type, version) to ensure version uniqueness per fact type
-- Note: This requires dropping existing data if there are duplicates
-- For now, we'll add a composite index instead
CREATE UNIQUE INDEX IF NOT EXISTS idx_kie_container_versions_fact_type_version 
ON kie_container_versions(fact_type, version);

COMMENT ON COLUMN decision_rules.fact_type IS 'Fact type this rule applies to (e.g., Declaration, Order, Customer). Determines which KieContainer the rule belongs to.';
COMMENT ON COLUMN kie_container_versions.fact_type IS 'Fact type this container version applies to (e.g., Declaration, Order, Customer).';

