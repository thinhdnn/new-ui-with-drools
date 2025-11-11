-- Create change_requests table for managing rule change requests per fact type
-- Each change request contains proposed changes that need approval before deployment

CREATE TABLE IF NOT EXISTS change_requests (
    id BIGSERIAL PRIMARY KEY,
    fact_type VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'Pending', -- Pending, Approved, Rejected
    changes_json JSONB, -- JSON object containing proposed changes: {rulesToAdd: [], rulesToUpdate: [], rulesToDelete: []}
    
    -- Approval information
    approved_by VARCHAR(255),
    approved_date TIMESTAMP,
    rejected_by VARCHAR(255),
    rejected_date TIMESTAMP,
    rejection_reason TEXT,
    
    -- Audit columns
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_date TIMESTAMP,
    last_modified_by VARCHAR(255)
);

COMMENT ON TABLE change_requests IS 'Stores change requests for rule modifications that require approval before deployment';
COMMENT ON COLUMN change_requests.fact_type IS 'Fact type this change request applies to (e.g., Declaration, CargoReport)';
COMMENT ON COLUMN change_requests.title IS 'Title/summary of the change request';
COMMENT ON COLUMN change_requests.description IS 'Detailed description of the proposed changes';
COMMENT ON COLUMN change_requests.status IS 'Status of the change request: Pending, Approved, Rejected';
COMMENT ON COLUMN change_requests.changes_json IS 'JSON object containing proposed changes: {rulesToAdd: [ruleIds], rulesToUpdate: [ruleIds], rulesToDelete: [ruleIds]}';
COMMENT ON COLUMN change_requests.approved_by IS 'User who approved the change request';
COMMENT ON COLUMN change_requests.approved_date IS 'Date when the change request was approved';
COMMENT ON COLUMN change_requests.rejected_by IS 'User who rejected the change request';
COMMENT ON COLUMN change_requests.rejected_date IS 'Date when the change request was rejected';
COMMENT ON COLUMN change_requests.rejection_reason IS 'Reason for rejection if the change request was rejected';

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_change_requests_fact_type ON change_requests(fact_type);
CREATE INDEX IF NOT EXISTS idx_change_requests_status ON change_requests(status);
CREATE INDEX IF NOT EXISTS idx_change_requests_created_date ON change_requests(created_date DESC);
CREATE INDEX IF NOT EXISTS idx_change_requests_fact_type_status ON change_requests(fact_type, status);

