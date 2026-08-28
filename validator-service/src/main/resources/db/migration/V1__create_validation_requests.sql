CREATE TABLE IF NOT EXISTS validation_requests (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    profile_id VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    idempotency_key VARCHAR(255) NOT NULL UNIQUE
);

CREATE INDEX IF NOT EXISTS idx_validation_requests_tenant_id
    ON validation_requests (tenant_id);

CREATE INDEX IF NOT EXISTS idx_validation_requests_profile_id
    ON validation_requests (profile_id);
