package com.owiseman.core.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Permission {
    private UUID id;
    private UUID realmId;
    private String resource;
    private String action;
    private LocalDateTime createdAt;

    public Permission() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRealmId() {
        return realmId;
    }

    public void setRealmId(UUID realmId) {
        this.realmId = realmId;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
