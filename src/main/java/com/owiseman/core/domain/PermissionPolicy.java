package com.owiseman.core.domain;

import java.util.UUID;

public class PermissionPolicy {
    private UUID permissionId;
    private UUID policyId;

    public PermissionPolicy() {
    }

    public UUID getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(UUID permissionId) {
        this.permissionId = permissionId;
    }

    public UUID getPolicyId() {
        return policyId;
    }

    public void setPolicyId(UUID policyId) {
        this.policyId = policyId;
    }
}
