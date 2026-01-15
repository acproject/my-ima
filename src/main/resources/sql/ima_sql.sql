CREATE TABLE IF NOT EXISTS ima_realm (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    config JSONB,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS ima_user (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    realm_id UUID NOT NULL,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) ,
    password_hash VARCHAR(255) ,
    enabled BOOLEAN DEFAULT TRUE,
    attributes JSONB,  -- 自定义属性（部门、工号、地区）
    UNIQUE (realm_id, username),
    created_at TIMESTAMP DEFAULT NOW(), 
    FOREIGN KEY (realm_id) REFERENCES ima_realm(id)
);


CREATE TABLE IF NOT EXISTS ima_client (
    id UUID PRIMARY KEY,
    realm_id UUID NOT NULL,
    client_id VARCHAR(255) NOT NULL,
    client_secret VARCHAR(255) ,
    type VARCHAR(128) NOT NULL, -- confidential, public
    redirect_uris TEXT[],
    scopes TEXT[],
    enabled BOOLEAN DEFAULT TRUE,
    config JSONB,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(realm_id, client_id),
    FOREIGN KEY (realm_id) REFERENCES ima_realm(id)
);

CREATE TABLE IF NOT EXISTS ima_role (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    realm_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (realm_id, name),
    FOREIGN KEY (realm_id) REFERENCES ima_realm(id)
);

CREATE TABLE IF NOT EXISTS ima_permission (
    id UUID PRIMARY KEY,
    realm_id UUID NOT NULL,
    resource VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (realm_id, resource, action),
    FOREIGN KEY (realm_id) REFERENCES ima_realm(id)
);

CREATE TABLE ima_role_permission (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES ima_role(id),
    FOREIGN KEY (permission_id) REFERENCES ima_permission(id)
);

CREATE TABLE ima_user_role (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES ima_user(id),
    FOREIGN KEY (role_id) REFERENCES ima_role(id)
);
-- "user.department == 'IT' && time.now < '18:00'"
CREATE TABLE ima_policy (
    id UUID PRIMARY KEY,
    realm_id UUID NOT NULL,
    type VARCHAR(128) NOT NULL, -- role / attribute / time / custom
    expression TEXT NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (realm_id) REFERENCES ima_realm(id)
);

CREATE TABLE ima_permission_policy (
    permission_id UUID NOT NULL,
    policy_id UUID NOT NULL,
    PRIMARY KEY (permission_id, policy_id),
    FOREIGN KEY (permission_id) REFERENCES ima_permission(id),
    FOREIGN KEY (policy_id) REFERENCES ima_policy(id)
);

CREATE TABLE ima_token (
    id UUID PRIMARY KEY,
    realm_id UUID NOT NULL,
    user_id UUID,
    client_id UUID NOT NULL,
    token_type VARCHAR(128) NOT NULL, -- access_token, refresh_token
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (realm_id) REFERENCES ima_realm(id),
    FOREIGN KEY (user_id) REFERENCES ima_user(id),
    FOREIGN KEY (client_id) REFERENCES ima_client(id)
);

CREATE TABLE ima_audit_log (
    id BIGSERIAL PRIMARY KEY,
    realm_id UUID ,
    event_type VARCHAR(128) ,-- login, logout, password_change, role_change
    subject_id UUID,
    subject_type VARCHAR(128) ,-- user, client
    ip_address VARCHAR(255),
    detial JSONB,
    created_at TIMESTAMP DEFAULT NOW()
);