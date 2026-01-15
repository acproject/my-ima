package com.owiseman.core.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Realm entity in API requests/responses.
 */
public class RealmDTO {

    private UUID id;
    
    @NotBlank(message = "Realm name is required")
    @Size(min = 1, max = 255, message = "Realm name must be between 1 and 255 characters")
    private String name;
    
    private Boolean enabled;
    
    private String config;
    
    private LocalDateTime createdAt;

    // Default constructor
    public RealmDTO() {}

    // Constructor with fields
    public RealmDTO(UUID id, String name, Boolean enabled, String config, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.enabled = enabled;
        this.config = config;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}