package com.owiseman.core.web.controller;

import com.owiseman.core.service.UserService;
import com.owiseman.core.web.dto.UserDTO;
import com.owiseman.core.web.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for User management.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Create a new user.
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO created = userService.createUser(userDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Get a user by ID.
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable UUID id) {
        UserDTO user = userService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
        return ResponseEntity.ok(user);
    }

    /**
     * Get a user by username.
     * GET /api/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));
        return ResponseEntity.ok(user);
    }

    /**
     * Get a user by email.
     * GET /api/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        UserDTO user = userService.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", email));
        return ResponseEntity.ok(user);
    }

    /**
     * Get all users in a realm.
     * GET /api/users/realm/{realmId}
     */
    @GetMapping("/realm/{realmId}")
    public ResponseEntity<List<UserDTO>> getUsersByRealm(
            @PathVariable UUID realmId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<UserDTO> users = userService.findByRealm(realmId, page, size);
        return ResponseEntity.ok(users);
    }

    /**
     * Get all users.
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<UserDTO> users = userService.findAll(page, size);
        return ResponseEntity.ok(users);
    }

    /**
     * Update a user.
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable UUID id, 
            @Valid @RequestBody UserDTO userDTO) {
        if (!id.equals(userDTO.getId())) {
            throw new IllegalArgumentException("ID in path must match ID in request body");
        }
        UserDTO updated = userService.update(userDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Enable a user.
     * POST /api/users/{id}/enable
     */
    @PostMapping("/{id}/enable")
    public ResponseEntity<UserDTO> enableUser(@PathVariable UUID id) {
        UserDTO user = userService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
        UserDTO enabled = userService.enable(user);
        return ResponseEntity.ok(enabled);
    }

    /**
     * Disable a user.
     * POST /api/users/{id}/disable
     */
    @PostMapping("/{id}/disable")
    public ResponseEntity<UserDTO> disableUser(@PathVariable UUID id) {
        UserDTO user = userService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
        UserDTO disabled = userService.disable(user);
        return ResponseEntity.ok(disabled);
    }

    /**
     * Delete a user.
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Assign a role to a user.
     * POST /api/users/{userId}/roles/{roleId}
     */
    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> assignRole(
            @PathVariable UUID userId, 
            @PathVariable UUID roleId) {
        userService.assignRole(userId, roleId);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove a role from a user.
     * DELETE /api/users/{userId}/roles/{roleId}
     */
    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> removeRole(
            @PathVariable UUID userId, 
            @PathVariable UUID roleId) {
        userService.removeRole(userId, roleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get user's roles.
     * GET /api/users/{userId}/roles
     */
    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<String>> getUserRoles(@PathVariable UUID userId) {
        List<String> roles = userService.findUserRoles(userId);
        return ResponseEntity.ok(roles);
    }

    /**
     * Get user's permissions.
     * GET /api/users/{userId}/permissions
     */
    @GetMapping("/{userId}/permissions")
    public ResponseEntity<List<String>> getUserPermissions(@PathVariable UUID userId) {
        List<String> permissions = userService.findPermissions(userId);
        return ResponseEntity.ok(permissions);
    }

    /**
     * Check if user has a specific permission.
     * GET /api/users/{userId}/permissions/{permission}
     */
    @GetMapping("/{userId}/permissions/{permission}")
    public ResponseEntity<Boolean> hasPermission(
            @PathVariable UUID userId, 
            @PathVariable String permission) {
        boolean hasPermission = userService.hasPermission(userId, permission);
        return ResponseEntity.ok(hasPermission);
    }

    /**
     * Count users in a realm.
     * GET /api/users/realm/{realmId}/count
     */
    @GetMapping("/realm/{realmId}/count")
    public ResponseEntity<Long> countUsersByRealm(@PathVariable UUID realmId) {
        long count = userService.countByRealm(realmId);
        return ResponseEntity.ok(count);
    }
}
