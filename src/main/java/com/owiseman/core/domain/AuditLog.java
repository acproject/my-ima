package com.owiseman.core.domain;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class AuditLog {
    private Long id;
    private UUID realmId;
    private AuditEventType eventType;
    private UUID subjectId;
    private AuditSubjectType subjectType;
    private String ipAddress;
    private Map<String, Object> detail;
    private LocalDateTime createdAt;

    public AuditLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getRealmId() {
        return realmId;
    }

    public void setRealmId(UUID realmId) {
        this.realmId = realmId;
    }

    public AuditEventType getEventType() {
        return eventType;
    }

    public void setEventType(AuditEventType eventType) {
        this.eventType = eventType;
    }

    public UUID getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(UUID subjectId) {
        this.subjectId = subjectId;
    }

    public AuditSubjectType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(AuditSubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Map<String, Object> getDetail() {
        return detail;
    }

    public void setDetail(Map<String, Object> detail) {
        this.detail = detail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
