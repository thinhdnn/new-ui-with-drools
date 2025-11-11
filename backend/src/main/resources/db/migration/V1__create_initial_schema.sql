-- Create initial database schema for Rule Engine

-- Create decision_rules table
CREATE TABLE IF NOT EXISTS decision_rules (
    id BIGSERIAL PRIMARY KEY,
    rule_name VARCHAR(255) NOT NULL,
    label VARCHAR(255),
    rule_content TEXT NOT NULL,
    rule_action VARCHAR(50),
    rule_result TEXT,
    rule_score NUMERIC(19, 2),
    priority INTEGER DEFAULT 0,
    active BOOLEAN DEFAULT true NOT NULL,
    
    -- Versioning columns
    version INTEGER DEFAULT 1 NOT NULL,
    parent_rule_id BIGINT,
    is_latest BOOLEAN DEFAULT true NOT NULL,
    version_notes TEXT,
    
    -- Audit columns
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    
    -- Foreign key for versioning
    CONSTRAINT fk_parent_rule FOREIGN KEY (parent_rule_id) REFERENCES decision_rules(id)
);

COMMENT ON TABLE decision_rules IS 'Stores decision rules with versioning support';
COMMENT ON COLUMN decision_rules.rule_content IS 'Complete DRL (Drools Rule Language) content for the rule';
COMMENT ON COLUMN decision_rules.version IS 'Version number of the rule';
COMMENT ON COLUMN decision_rules.parent_rule_id IS 'Reference to the original rule (for versions)';
COMMENT ON COLUMN decision_rules.is_latest IS 'Indicates if this is the latest version';
COMMENT ON COLUMN decision_rules.version_notes IS 'Notes about changes in this version';

-- Note: Declaration entity is NOT persisted - it's only used as a Drools fact
-- No declarations table needed

-- Create rule_execution_results table
-- Note: declaration_id is stored as VARCHAR (declaration identifier) instead of FK
-- because Declaration entity is not persisted
CREATE TABLE IF NOT EXISTS rule_execution_results (
    id BIGSERIAL PRIMARY KEY,
    declaration_id VARCHAR(255) NOT NULL,  -- Changed from BIGINT FK to VARCHAR identifier
    decision_rule_id BIGINT NOT NULL,      -- Renamed from rule_id to match entity
    matched BOOLEAN NOT NULL DEFAULT false,
    rule_action VARCHAR(50),
    rule_result TEXT,
    rule_score NUMERIC(5, 2),
    executed_at TIMESTAMP NOT NULL,
    
    -- Audit columns
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    
    -- Foreign key to decision_rules only
    CONSTRAINT fk_execution_rule FOREIGN KEY (decision_rule_id) REFERENCES decision_rules(id) ON DELETE CASCADE
);

COMMENT ON TABLE rule_execution_results IS 'Stores results of rule executions';
COMMENT ON COLUMN rule_execution_results.declaration_id IS 'Declaration identifier (not FK, as Declaration is not persisted)';

-- Create indexes for better query performance

-- Decision rules indexes
CREATE INDEX IF NOT EXISTS idx_decision_rules_active ON decision_rules(active);
CREATE INDEX IF NOT EXISTS idx_decision_rules_priority ON decision_rules(priority);
CREATE INDEX IF NOT EXISTS idx_decision_rules_parent ON decision_rules(parent_rule_id);
CREATE INDEX IF NOT EXISTS idx_decision_rules_latest ON decision_rules(is_latest);
CREATE INDEX IF NOT EXISTS idx_decision_rules_version ON decision_rules(version);

-- Rule execution results indexes
CREATE INDEX IF NOT EXISTS idx_execution_declaration ON rule_execution_results(declaration_id);
CREATE INDEX IF NOT EXISTS idx_execution_rule ON rule_execution_results(decision_rule_id);
CREATE INDEX IF NOT EXISTS idx_execution_time ON rule_execution_results(executed_at);
CREATE INDEX IF NOT EXISTS idx_execution_action ON rule_execution_results(rule_action);
CREATE INDEX IF NOT EXISTS idx_execution_matched ON rule_execution_results(matched);

-- ========== RULE CONDITION SCHEMA ==========
-- Relational schema for storing rule logic operations (groups and conditions)

-- Group types for logical grouping
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'group_type') THEN
        CREATE TYPE group_type AS ENUM ('AND','OR','NOT');
    END IF;
END$$;

-- Value types for type-safe condition values
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'value_type') THEN
        CREATE TYPE value_type AS ENUM (
            'STRING','INT','LONG','BIG_DECIMAL','DATE','BOOLEAN','ENUM','ARRAY','JSON'
        );
    END IF;
END$$;

-- Operator types for condition operators
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'operator_type') THEN
        CREATE TYPE operator_type AS ENUM (
            'EQUALS','NOT_EQUALS','GT','GTE','LT','LTE',
            'IN','NOT_IN','BETWEEN',
            'STR_CONTAINS','STR_STARTS_WITH','STR_ENDS_WITH','MATCHES',
            'IS_NULL','IS_NOT_NULL'
        );
    END IF;
END$$;

-- Rule condition groups table (supports nested AND/OR/NOT logic)
CREATE TABLE IF NOT EXISTS rule_condition_group (
    id              BIGSERIAL PRIMARY KEY,
    decision_rule_id BIGINT NOT NULL REFERENCES decision_rules(id) ON DELETE CASCADE,
    parent_id       BIGINT REFERENCES rule_condition_group(id) ON DELETE CASCADE,
    type            group_type NOT NULL,
    order_index     INT NOT NULL DEFAULT 0,
    
    -- Audit columns
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255)
);

COMMENT ON TABLE rule_condition_group IS 'Stores logical groups (AND/OR/NOT) for rule conditions';
COMMENT ON COLUMN rule_condition_group.decision_rule_id IS 'Reference to the rule this group belongs to';
COMMENT ON COLUMN rule_condition_group.parent_id IS 'Reference to parent group (for nested logic)';
COMMENT ON COLUMN rule_condition_group.type IS 'Logical operator: AND, OR, or NOT';
COMMENT ON COLUMN rule_condition_group.order_index IS 'Order within parent group or rule';

-- Rule conditions table (leaf conditions with field, operator, and typed value)
CREATE TABLE IF NOT EXISTS rule_condition (
    id              BIGSERIAL PRIMARY KEY,
    group_id        BIGINT NOT NULL REFERENCES rule_condition_group(id) ON DELETE CASCADE,
    field_path      VARCHAR(512) NOT NULL,
    operator        operator_type NOT NULL,
    value_type      value_type NOT NULL,
    
    -- Value columns (only one should be used depending on value_type)
    value_text      TEXT,
    value_number    BIGINT,
    value_decimal   NUMERIC(38,12),
    value_boolean   BOOLEAN,
    value_date      TIMESTAMP,
    value_json      JSONB,
    
    -- Additional options (e.g., case sensitivity, regex flags)
    options_json    JSONB,
    order_index     INT NOT NULL DEFAULT 0,
    
    -- Audit columns
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255)
);

COMMENT ON TABLE rule_condition IS 'Stores individual rule conditions with field path, operator, and typed value';
COMMENT ON COLUMN rule_condition.field_path IS 'Path to the field (e.g., declaration.invoiceAmount)';
COMMENT ON COLUMN rule_condition.operator IS 'Operator to apply (EQUALS, GT, STR_CONTAINS, etc.)';
COMMENT ON COLUMN rule_condition.value_type IS 'Type of the value (STRING, INT, BIG_DECIMAL, etc.)';
COMMENT ON COLUMN rule_condition.value_text IS 'Value for STRING, ENUM types';
COMMENT ON COLUMN rule_condition.value_number IS 'Value for INT, LONG types';
COMMENT ON COLUMN rule_condition.value_decimal IS 'Value for BIG_DECIMAL type';
COMMENT ON COLUMN rule_condition.value_boolean IS 'Value for BOOLEAN type';
COMMENT ON COLUMN rule_condition.value_date IS 'Value for DATE type';
COMMENT ON COLUMN rule_condition.value_json IS 'Value for ARRAY, JSON types';
COMMENT ON COLUMN rule_condition.options_json IS 'Additional options (case sensitivity, regex flags, etc.)';

-- Indexes for rule condition groups
CREATE INDEX IF NOT EXISTS idx_rcg_rule ON rule_condition_group(decision_rule_id);
CREATE INDEX IF NOT EXISTS idx_rcg_parent ON rule_condition_group(parent_id);
CREATE INDEX IF NOT EXISTS idx_rcg_type ON rule_condition_group(type);

-- Indexes for rule conditions (optimized for reporting)
CREATE INDEX IF NOT EXISTS idx_rc_group ON rule_condition(group_id);
CREATE INDEX IF NOT EXISTS idx_rc_field ON rule_condition(field_path);
CREATE INDEX IF NOT EXISTS idx_rc_operator ON rule_condition(operator);
CREATE INDEX IF NOT EXISTS idx_rc_vtype ON rule_condition(value_type);

-- ========== RULE OUTPUT SCHEMA ==========
-- Relational schema for storing rule outputs (groups and outputs)
-- Similar structure to rule_condition_group and rule_condition

-- Rule output groups table (supports nested AND/OR/NOT logic for outputs)
CREATE TABLE IF NOT EXISTS rule_output_group (
    id              BIGSERIAL PRIMARY KEY,
    decision_rule_id BIGINT NOT NULL REFERENCES decision_rules(id) ON DELETE CASCADE,
    parent_id       BIGINT REFERENCES rule_output_group(id) ON DELETE CASCADE,
    type            group_type NOT NULL,
    order_index     INT NOT NULL DEFAULT 0,
    
    -- Audit columns
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255)
);

COMMENT ON TABLE rule_output_group IS 'Stores logical groups (AND/OR/NOT) for rule outputs';
COMMENT ON COLUMN rule_output_group.decision_rule_id IS 'Reference to the rule this group belongs to';
COMMENT ON COLUMN rule_output_group.parent_id IS 'Reference to parent group (for nested logic)';
COMMENT ON COLUMN rule_output_group.type IS 'Logical operator: AND, OR, or NOT';
COMMENT ON COLUMN rule_output_group.order_index IS 'Order within parent group or rule';

-- Rule outputs table (individual outputs with action, result, and score)
CREATE TABLE IF NOT EXISTS rule_output (
    id              BIGSERIAL PRIMARY KEY,
    group_id        BIGINT NOT NULL REFERENCES rule_output_group(id) ON DELETE CASCADE,
    decision_rule_id BIGINT REFERENCES decision_rules(id) ON DELETE CASCADE,
    action          VARCHAR(50),
    result          TEXT,
    score           NUMERIC(5, 2),
    flag            VARCHAR(255),
    document_type   VARCHAR(255),
    document_id     VARCHAR(255),
    description     TEXT,
    order_index     INT NOT NULL DEFAULT 0,
    
    -- Audit columns
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255)
);

COMMENT ON TABLE rule_output IS 'Stores individual rule outputs with action, result message, and risk score';
COMMENT ON COLUMN rule_output.group_id IS 'Reference to the output group this output belongs to';
COMMENT ON COLUMN rule_output.decision_rule_id IS 'Reference to the decision rule (for convenience)';
COMMENT ON COLUMN rule_output.action IS 'Action to take (FLAG, APPROVE, REJECT, REVIEW, HOLD)';
COMMENT ON COLUMN rule_output.result IS 'Result message/description when rule matches';
COMMENT ON COLUMN rule_output.score IS 'Risk score to assign (0-100)';
COMMENT ON COLUMN rule_output.flag IS 'Optional flag label/category (e.g., HIGH_RISK, SUSPICIOUS)';
COMMENT ON COLUMN rule_output.document_type IS 'Optional related document type (e.g., INVOICE, LICENSE)';
COMMENT ON COLUMN rule_output.document_id IS 'Optional related document identifier';
COMMENT ON COLUMN rule_output.description IS 'Optional longer description for reporting';
COMMENT ON COLUMN rule_output.order_index IS 'Order within the group';

-- Indexes for rule output groups
CREATE INDEX IF NOT EXISTS idx_rog_rule ON rule_output_group(decision_rule_id);
CREATE INDEX IF NOT EXISTS idx_rog_parent ON rule_output_group(parent_id);
CREATE INDEX IF NOT EXISTS idx_rog_type ON rule_output_group(type);

-- Indexes for rule outputs
CREATE INDEX IF NOT EXISTS idx_ro_group ON rule_output(group_id);
CREATE INDEX IF NOT EXISTS idx_ro_rule ON rule_output(decision_rule_id);
CREATE INDEX IF NOT EXISTS idx_ro_action ON rule_output(action);

