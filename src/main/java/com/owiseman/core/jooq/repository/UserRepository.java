package com.owiseman.core.jooq.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.owiseman.core.domain.User;

/**
 * Repository interface for User entity CRUD operations.
 * Provides methods for user management including authentication and authorization support.
 */
public interface UserRepository {

    /**
     * Find a user by its unique ID.
     *
     * @param id the unique identifier of the user
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findById(UUID id);

    /**
     * Find a user by username within a specific realm.
     *
     * @param realmId the realm ID scope
     * @param username the username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(UUID realmId, String username);

    /**
     * Find a user by email within a specific realm.
     *
     * @param realmId the realm ID scope
     * @param email the email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(UUID realmId, String email);

    /**
     * Find all users within a realm with pagination.
     *
     * @param realmId the realm ID scope
     * @param offset number of records to skip (for pagination)
     * @param limit maximum number of records to return
     * @return list of users, ordered by creation date (newest first)
     */
    List<User> findAll(UUID realmId, int offset, int limit);

    /**
     * Create a new user with auto-generated UUID.
     * Password should be hashed before storage (BCrypt).
     *
     * @param user the user to create
     * @return the created user with generated ID
     */
    User create(User user);

    /**
     * Update an existing user.
     * Password hash is not changed if not provided.
     *
     * @param user the user with updated fields
     * @return the updated user
     */
    User update(User user);

    /**
     * Soft delete a user by ID.
     * Associated records in user_role table will be removed via cascade.
     *
     * @param id the user ID to delete
     */
    void delete(UUID id);

    /**
     * Find all permission identifiers for a user.
     * Permissions are derived through user -> role -> permission relationship.
     *
     * @param userId the user ID
     * @return list of permission identifiers (e.g., "users:read", "users:write")
     */
    List<String> findPermissions(UUID userId);

    /**
     * Assign a role to a user.
     * Creates a new record in user_role table.
     * Same role cannot be assigned twice to the same user (unique constraint).
     *
     * @param userId the user ID
     * @param roleId the role ID to assign
     */
    void assignRole(UUID userId, UUID roleId);

    /**
     * Remove a role from a user.
     * Deletes the corresponding record from user_role table.
     *
     * @param userId the user ID
     * @param roleId the role ID to remove
     */
    void removeRole(UUID userId, UUID roleId);

    /**
     * Find all role identifiers assigned to a user.
     *
     * @param userId the user ID
     * @return list of role identifiers
     */
    List<String> findUserRoles(UUID userId);

    long countByRealm(UUID realmId);
}
