-- Initial schema

-- Ensure extension for UUID generation exists
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Create table: client
CREATE TABLE IF NOT EXISTS client (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index on client.name for fast lookup on existsByNameIgnoreCase
CREATE INDEX IF NOT EXISTS idx_client_name ON client (LOWER(name));

-- Create table: app_user
CREATE TABLE IF NOT EXISTS app_user (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    client_id UUID NOT NULL,
    CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);

-- Indexes for performance and multi-tenancy access
CREATE INDEX IF NOT EXISTS idx_app_user_client_id ON app_user (client_id);
CREATE INDEX IF NOT EXISTS idx_app_user_client_id_username ON app_user (client_id, username);
CREATE INDEX IF NOT EXISTS idx_app_user_client_id_id ON app_user (client_id, id);

-- seed admin client and user
INSERT INTO client (id, name, status, created_at)
VALUES ('00000000-0000-0000-0000-000000000001', 'Default Client', 'ACTIVE', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO app_user (id, username, password, role, enabled, client_id)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'admin',
    '$2a$10$rQYknmGgDt8OG14cZ3vvd.5FH84AImKzBNcpQaBpwIVqbn.Y0i8ea',
    'ADMIN',
    true,
    '00000000-0000-0000-0000-000000000001'
)
ON CONFLICT (id) DO NOTHING;
