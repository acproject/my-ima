package com.owiseman.core.domain;

import java.util.UUID;

public class RolePermission {
    private UUID roleId;
    private UUID permissionId;

    public RolePermission() {
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public UUID getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(UUID permissionId) {
        this.permissionId = permissionId;
    }
}
