package com.owiseman.core.jooq.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.owiseman.core.domain.Role;

/**
 * Repository interface for Role entity CRUD operations.
 * Provides methods for role management including permission assignments.
 */
public interface RoleRepository {

    /**
     * Find a role by its unique ID.
     *
     * @param id the unique identifier of the role
     * @return Optional containing the role if found, empty otherwise
     */
    Optional<Role> findById(UUID id);

    /**
     * Find a role by name within a specific realm.
     *
     * @param realmId the realm ID scope
     * @param name the role name to search for
     * @return Optional containing the role if found, empty otherwise
     */
    Optional<Role> findByName(UUID realmId, String name);

    /**
     * Find all roles within a realm with pagination.
     *
     * @param realmId the realm ID scope
     * @param offset number of records to skip (for pagination)
     * @param limit maximum number of records to return
     * @return list of roles
     */
    List<Role> findAll(UUID realmId, int offset, int limit);

    /**
     * Create a new role with auto-generated UUID.
     *
     * @param role the role to create
     * @return the created role with generated ID
     */
    Role create(Role role);

    /**
     * Update an existing role.
     *
     * @param role the role with updated fields
     * @return the updated role
     */
    Role update(Role role);

    /**
     * Delete a role by ID.
     *
     * @param id the role ID to delete
     */
    void delete(UUID id);

    /**
     * Find all permissions assigned to a role.
     *
     * @param roleId the role ID
     * @return list of permission identifiers
     */
    List<String> findPermissions(UUID roleId);

    /**
     * Assign a permission to a role.
     *
     * @param roleId the role ID
     * @param permissionId the permission ID to assign
     */
    void assignPermission(UUID roleId, UUID permissionId);

    /**
     * Remove a permission from a role.
     *
     * @param roleId the role ID
     * @param permissionId the permission ID to remove
     */
    void removePermission(UUID roleId, UUID permissionId);

    long countByRealm(UUID realmId);
}
