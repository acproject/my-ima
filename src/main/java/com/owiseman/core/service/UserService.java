package com.owiseman.core.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.owiseman.core.web.dto.UserDTO;

/**
 * Service interface for User business logic operations.
 */
public interface UserService {

    /**
     * Create a new user.
     *
     * @param userDTO the user data
     * @return the created user
     */
    UserDTO createUser(UserDTO userDTO);

    /**
     * Find a user by ID.
     *
     * @param id the user ID
     * @return Optional containing the user DTO if found
     */
    Optional<UserDTO> findById(UUID id);

    /**
     * Find a user by username.
     *
     * @param username the username
     * @return Optional containing the user DTO if found
     */
    Optional<UserDTO> findByUsername(String username);

    /**
     * Find a user by email.
     *
     * @param email the email
     * @return Optional containing the user DTO if found
     */
    Optional<UserDTO> findByEmail(String email);

    /**
     * Get all users in a realm with pagination.
     *
     * @param realmId the realm ID
     * @param page page number (0-based)
     * @param size page size
     * @return list of user DTOs
     */
    List<UserDTO> findByRealm(UUID realmId, int page, int size);

    /**
     * Get all users with pagination.
     *
     * @param page page number (0-based)
     * @param size page size
     * @return list of user DTOs
     */
    List<UserDTO> findAll(int page, int size);

    /**
     * Update a user.
     *
     * @param userDTO the user with updated fields
     * @return the updated user
     */
    UserDTO update(UserDTO userDTO);

    /**
     * Enable a user.
     *
     * @param userDTO the user to enable
     * @return the enabled user
     */
    UserDTO enable(UserDTO userDTO);

    /**
     * Disable a user.
     *
     * @param userDTO the user to disable
     * @return the disabled user
     */
    UserDTO disable(UserDTO userDTO);

    /**
     * Delete a user.
     *
     * @param id the user ID
     */
    void delete(UUID id);

    /**
     * Assign a role to a user.
     *
     * @param userId the user ID
     * @param roleId the role ID
     */
    void assignRole(UUID userId, UUID roleId);

    /**
     * Remove a role from a user.
     *
     * @param userId the user ID
     * @param roleId the role ID
     */
    void removeRole(UUID userId, UUID roleId);

    /**
     * Get all roles assigned to a user.
     *
     * @param userId the user ID
     * @return list of role names
     */
    List<String> findUserRoles(UUID userId);

    /**
     * Get all permissions for a user through their roles.
     *
     * @param userId the user ID
     * @return list of permission identifiers (e.g., "users:read", "users:write")
     */
    List<String> findPermissions(UUID userId);

    /**
     * Check if a user has a specific permission.
     *
     * @param userId the user ID
     * @param permission the permission identifier
     * @return true if user has the permission
     */
    boolean hasPermission(UUID userId, String permission);

    /**
     * Check if user exists and is enabled.
     *
     * @param userId the user ID
     * @return true if user exists and is enabled
     */
    boolean isActive(UUID userId);

    /**
     * Get the count of users in a realm.
     *
     * @param realmId the realm ID
     * @return count of users
     */
    long countByRealm(UUID realmId);
}