-- MY-IMA IAM Server Database Schema
-- Initial migration: Create core tables for realm-based authentication

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Create enum types
CREATE TYPE token_type AS ENUM ('ACCESS', 'REFRESH', 'ID');

CREATE TYPE audit_event_type AS ENUM (
    'USER_LOGIN', 'USER_LOGOUT', 'USER_CREATED', 'USER_UPDATED', 'USER_DELETED',
    'ROLE_CREATED', 'ROLE_UPDATED', 'ROLE_DELETED',
    'PERMISSION_GRANTED', 'PERMISSION_REVOKED',
    'REALM_CREATED', 'REALM_UPDATED', 'REALM_DELETED', 'REALM_ENABLED', 'REALM_DISABLED'
);

-- Realms table: Multi-tenant realm configuration
CREATE TABLE IF NOT EXISTS ima_realm (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(255),
    description TEXT,
    enabled BOOLEAN NOT NULL DEFAULT true,
    configuration JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Create index for realm name lookups
CREATE INDEX IF NOT EXISTS idx_ima_realm_name ON ima_realm(name);
CREATE INDEX IF NOT EXISTS idx_ima_realm_enabled ON ima_realm(enabled);

-- Users table: User accounts with realm association
CREATE TABLE IF NOT EXISTS ima_user (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    realm_id UUID NOT NULL REFERENCES ima_realm(id) ON DELETE CASCADE,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    enabled BOOLEAN NOT NULL DEFAULT true,
    attributes JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    last_login TIMESTAMP WITH TIME ZONE,
    UNIQUE(realm_id, username),
    UNIQUE(realm_id, email)
);

-- Create indexes for user lookups
CREATE INDEX IF NOT EXISTS idx_ima_user_realm ON ima_user(realm_id);
CREATE INDEX IF NOT EXISTS idx_ima_user_username ON ima_user(realm_id, username);
CREATE INDEX IF NOT EXISTS idx_ima_user_email ON ima_user(realm_id, email);
CREATE INDEX IF NOT EXISTS idx_ima_user_enabled ON ima_user(enabled);

-- Roles table: Role definitions per realm
CREATE TABLE IF NOT EXISTS ima_role (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    realm_id UUID NOT NULL REFERENCES ima_realm(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE(realm_id, name)
);

-- Create indexes for role lookups
CREATE INDEX IF NOT EXISTS idx_ima_role_realm ON ima_role(realm_id);

-- Permissions table: Permission definitions per realm
CREATE TABLE IF NOT EXISTS ima_permission (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    realm_id UUID NOT NULL REFERENCES ima_realm(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    resource VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    attributes JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE(realm_id, name),
    UNIQUE(realm_id, resource, action)
);

-- Create indexes for permission lookups
CREATE INDEX IF NOT EXISTS idx_ima_permission_realm ON ima_permission(realm_id);
CREATE INDEX IF NOT EXISTS idx_ima_permission_resource ON ima_permission(resource, action);

-- Role-Permission association table
CREATE TABLE IF NOT EXISTS ima_role_permission (
    role_id UUID NOT NULL REFERENCES ima_role(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES ima_permission(id) ON DELETE CASCADE,
    granted_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    granted_by UUID REFERENCES ima_user(id),
    PRIMARY KEY (role_id, permission_id)
);

-- Create index for role-permission lookups
CREATE INDEX IF NOT EXISTS idx_ima_role_permission_role ON ima_role_permission(role_id);

-- User-Role association table
CREATE TABLE IF NOT EXISTS ima_user_role (
    user_id UUID NOT NULL REFERENCES ima_user(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES ima_role(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    assigned_by UUID REFERENCES ima_user(id),
    PRIMARY KEY (user_id, role_id)
);

-- Create index for user-role lookups
CREATE INDEX IF NOT EXISTS idx_ima_user_role_user ON ima_user_role(user_id);

-- Refresh tokens table: For token refresh functionality
CREATE TABLE IF NOT EXISTS ima_refresh_token (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES ima_user(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    revoked_at TIMESTAMP WITH TIME ZONE,
    revoked_reason VARCHAR(255),
    replaced_by UUID REFERENCES ima_refresh_token(id),
    user_agent TEXT,
    client_ip VARCHAR(45)
);

-- Create indexes for refresh token lookups
CREATE INDEX IF NOT EXISTS idx_ima_refresh_token_user ON ima_refresh_token(user_id);
CREATE INDEX IF NOT EXISTS idx_ima_refresh_token_expires ON ima_refresh_token(expires_at);

-- Audit log table: For security and compliance
CREATE TABLE IF NOT EXISTS ima_audit_log (
    id BIGSERIAL PRIMARY KEY,
    realm_id UUID REFERENCES ima_realm(id) ON DELETE SET NULL,
    user_id UUID REFERENCES ima_user(id) ON DELETE SET NULL,
    event_type audit_event_type NOT NULL,
    event_data JSONB DEFAULT '{}',
    resource_type VARCHAR(100),
    resource_id VARCHAR(255),
    ip_address VARCHAR(45),
    user_agent TEXT,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Create indexes for audit log queries
CREATE INDEX IF NOT EXISTS idx_ima_audit_log_realm ON ima_audit_log(realm_id);
CREATE INDEX IF NOT EXISTS idx_ima_audit_log_user ON ima_audit_log(user_id);
CREATE INDEX IF NOT EXISTS idx_ima_audit_log_event ON ima_audit_log(event_type);
CREATE INDEX IF NOT EXISTS idx_ima_audit_log_timestamp ON ima_audit_log(timestamp);

-- Clients table: OAuth2/OIDC client applications
CREATE TABLE IF NOT EXISTS ima_client (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    realm_id UUID NOT NULL REFERENCES ima_realm(id) ON DELETE CASCADE,
    client_id VARCHAR(100) NOT NULL,
    client_secret VARCHAR(255),
    client_name VARCHAR(255) NOT NULL,
    description TEXT,
    enabled BOOLEAN NOT NULL DEFAULT true,
    public_client BOOLEAN NOT NULL DEFAULT false,
    redirect_uris TEXT[] DEFAULT '{}',
    grant_types TEXT[] DEFAULT '{}',
    response_types TEXT[] DEFAULT '{}',
    scopes TEXT[] DEFAULT '{}',
    access_token_lifetime INTEGER DEFAULT 300,
    refresh_token_lifetime INTEGER DEFAULT 3600,
    attributes JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE(realm_id, client_id)
);

-- Create indexes for client lookups
CREATE INDEX IF NOT EXISTS idx_ima_client_realm ON ima_client(realm_id);

-- Policies table: Authorization policies
CREATE TABLE IF NOT EXISTS ima_policy (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    realm_id UUID NOT NULL REFERENCES ima_realm(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    expression TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE(realm_id, name)
);

-- Create indexes for policy lookups
CREATE INDEX IF NOT EXISTS idx_ima_policy_realm ON ima_policy(realm_id);

-- Permission policies: Links policies to permissions
CREATE TABLE IF NOT EXISTS ima_permission_policy (
    permission_id UUID NOT NULL REFERENCES ima_permission(id) ON DELETE CASCADE,
    policy_id UUID NOT NULL REFERENCES ima_policy(id) ON DELETE CASCADE,
    PRIMARY KEY (permission_id, policy_id)
);

-- Tokens table: For access/refresh token lifecycle management
CREATE TABLE IF NOT EXISTS ima_token (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    realm_id UUID NOT NULL REFERENCES ima_realm(id) ON DELETE CASCADE,
    user_id UUID REFERENCES ima_user(id) ON DELETE CASCADE,
    client_id UUID REFERENCES ima_client(id) ON DELETE CASCADE,
    token_type VARCHAR(32) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_ima_token_realm ON ima_token(realm_id);
CREATE INDEX IF NOT EXISTS idx_ima_token_user_type ON ima_token(user_id, token_type);
CREATE INDEX IF NOT EXISTS idx_ima_token_client_type ON ima_token(client_id, token_type);
CREATE INDEX IF NOT EXISTS idx_ima_token_expires ON ima_token(expires_at);

-- Add comments to tables
COMMENT ON TABLE ima_realm IS 'Multi-tenant realm configuration for IAM';
COMMENT ON TABLE ima_user IS 'User accounts with realm-based organization';
COMMENT ON TABLE ima_role IS 'Role definitions per realm for RBAC';
COMMENT ON TABLE ima_permission IS 'Permission definitions per realm';
COMMENT ON TABLE ima_refresh_token IS 'Refresh tokens for JWT token refresh';
COMMENT ON TABLE ima_audit_log IS 'Audit trail for security and compliance';
COMMENT ON TABLE ima_client IS 'OAuth2/OIDC client applications';
COMMENT ON TABLE ima_policy IS 'Authorization policies for fine-grained access control';
COMMENT ON TABLE ima_token IS 'JWT token lifecycle management';
