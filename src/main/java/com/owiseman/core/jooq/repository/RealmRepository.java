package com.owiseman.core.jooq.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.owiseman.core.domain.Realm;

/**
 * Repository interface for Realm entity CRUD operations.
 * Provides methods to manage realms in a multi-tenant environment.
 */
public interface RealmRepository {

    /**
     * Find a realm by its unique ID.
     *
     * @param id the unique identifier of the realm
     * @return Optional containing the realm if found, empty otherwise
     */
    Optional<Realm> findById(UUID id);

    /**
     * Find a realm by name within a specific realm scope.
     *
     * @param realmId the realm ID scope (for context)
     * @param name the realm name to search for
     * @return Optional containing the realm if found, empty otherwise
     */
    Optional<Realm> findByName(UUID realmId, String name);

    /**
     * Find all realms with pagination.
     *
     * @param realmId optional realm ID filter
     * @param offset number of records to skip
     * @param limit maximum number of records to return
     * @return list of realms
     */
    List<Realm> findAll(UUID realmId, int offset, int limit);

    /**
     * Create a new realm with auto-generated UUID.
     *
     * @param realm the realm to create
     * @return the created realm with generated ID
     */
    Realm create(Realm realm);

    /**
     * Update an existing realm.
     *
     * @param realm the realm with updated fields
     * @return the updated realm
     */
    Realm update(Realm realm);

    /**
     * Enable a realm by setting its enabled status to TRUE.
     *
     * @param id the realm ID to enable
     * @return the updated realm
     */
    Realm enable(UUID id);

    /**
     * Disable a realm by setting its enabled status to FALSE.
     *
     * @param id the realm ID to disable
     * @return the updated realm
     */
    Realm disable(UUID id);

    /**
     * Delete a realm by ID.
     *
     * @param id the realm ID to delete
     */
    void delete(UUID id);

    long count();
}
