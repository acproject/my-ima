package com.owiseman.core.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Policy {
    private UUID id;
    private UUID realmId;
    private PolicyType type;
    private String expression;
    private String description;
    private LocalDateTime createdAt;

    public Policy() {
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

    public PolicyType getType() {
        return type;
    }

    public void setType(PolicyType type) {
        this.type = type;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
