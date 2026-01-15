package com.owiseman.core.web.controller;

import com.owiseman.core.domain.Role;
import com.owiseman.core.exception.ResourceNotFoundException;
import com.owiseman.core.jooq.repository.RoleRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for Role management.
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Create a new role.
     * POST /api/roles
     */
    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        Role role = new Role();
        role.setRealmId(roleDTO.getRealmId());
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        
        Role created = roleRepository.create(role);
        return new ResponseEntity<>(mapToDTO(created), HttpStatus.CREATED);
    }

    /**
     * Get a role by ID.
     * GET /api/roles/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRole(@PathVariable UUID id) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role", id.toString()));
        return ResponseEntity.ok(mapToDTO(role));
    }

    /**
     * Get a role by name.
     * GET /api/roles/realm/{realmId}/name/{name}
     */
    @GetMapping("/realm/{realmId}/name/{name}")
    public ResponseEntity<RoleDTO> getRoleByName(
            @PathVariable UUID realmId, 
            @PathVariable String name) {
        Role role = roleRepository.findByName(realmId, name)
            .orElseThrow(() -> new ResourceNotFoundException("Role", name));
        return ResponseEntity.ok(mapToDTO(role));
    }

    /**
     * Get all roles in a realm.
     * GET /api/roles/realm/{realmId}
     */
    @GetMapping("/realm/{realmId}")
    public ResponseEntity<List<RoleDTO>> getRolesByRealm(
            @PathVariable UUID realmId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<RoleDTO> roles = roleRepository.findAll(realmId, page * size, size)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    /**
     * Get all roles.
     * GET /api/roles
     */
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<RoleDTO> roles = roleRepository.findAll(null, page * size, size)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    /**
     * Update a role.
     * PUT /api/roles/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole(
            @PathVariable UUID id, 
            @Valid @RequestBody RoleDTO roleDTO) {
        Role existingRole = roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role", id.toString()));
        
        existingRole.setName(roleDTO.getName());
        existingRole.setDescription(roleDTO.getDescription());
        
        Role updated = roleRepository.update(existingRole);
        return ResponseEntity.ok(mapToDTO(updated));
    }

    /**
     * Delete a role.
     * DELETE /api/roles/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role", id.toString()));
        roleRepository.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get role's permissions.
     * GET /api/roles/{id}/permissions
     */
    @GetMapping("/{id}/permissions")
    public ResponseEntity<List<String>> getRolePermissions(@PathVariable UUID id) {
        List<String> permissions = roleRepository.findPermissions(id);
        return ResponseEntity.ok(permissions);
    }

    /**
     * Assign permission to role.
     * POST /api/roles/{roleId}/permissions/{permissionId}
     */
    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> assignPermission(
            @PathVariable UUID roleId, 
            @PathVariable UUID permissionId) {
        roleRepository.assignPermission(roleId, permissionId);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove permission from role.
     * DELETE /api/roles/{roleId}/permissions/{permissionId}
     */
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> removePermission(
            @PathVariable UUID roleId, 
            @PathVariable UUID permissionId) {
        roleRepository.removePermission(roleId, permissionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Count roles in a realm.
     * GET /api/roles/realm/{realmId}/count
     */
    @GetMapping("/realm/{realmId}/count")
    public ResponseEntity<Long> countRolesByRealm(@PathVariable UUID realmId) {
        long count = roleRepository.countByRealm(realmId);
        return ResponseEntity.ok(count);
    }

    /**
     * Map domain entity to DTO.
     */
    private RoleDTO mapToDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setRealmId(role.getRealmId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setCreatedAt(role.getCreatedAt());
        return dto;
    }

    /**
     * DTO for Role entity in API requests/responses.
     */
    public static class RoleDTO {
        private UUID id;
        
        @NotBlank(message = "Realm ID is required")
        private UUID realmId;
        
        @NotBlank(message = "Role name is required")
        @Size(min = 1, max = 255, message = "Role name must be between 1 and 255 characters")
        private String name;
        
        private String description;
        
        private LocalDateTime createdAt;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
}
