-- Add column to store detailed rule changes (added, removed, updated rules)

ALTER TABLE kie_container_versions 
ADD COLUMN IF NOT EXISTS rule_changes_json JSONB;

COMMENT ON COLUMN kie_container_versions.rule_changes_json IS 'JSON object containing detailed rule changes: {added: [ruleIds], removed: [ruleIds], updated: [ruleIds]}';

