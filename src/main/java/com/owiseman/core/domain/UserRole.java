package com.owiseman.core.domain;

import java.util.UUID;

public class UserRole {
    private UUID userId;
    private UUID roleId;

    public UserRole() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }
}
