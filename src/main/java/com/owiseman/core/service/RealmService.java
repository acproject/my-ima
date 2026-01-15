package com.owiseman.core.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.owiseman.core.web.dto.RealmDTO;

/**
 * Service interface for Realm business logic operations.
 * Provides domain-specific operations beyond basic CRUD.
 */
public interface RealmService {

    /**
     * Create a new realm with auto-generated ID.
     *
     * @param realmDTO the realm data
     * @return the created realm
     */
    RealmDTO createRealm(RealmDTO realmDTO);

    /**
     * Find a realm by ID.
     *
     * @param id the realm ID
     * @return Optional containing the realm DTO if found
     */
    Optional<RealmDTO> findById(UUID id);

    /**
     * Find a realm by name.
     *
     * @param name the realm name
     * @return Optional containing the realm DTO if found
     */
    Optional<RealmDTO> findByName(String name);

    /**
     * Get all realms with pagination.
     *
     * @param page page number (0-based)
     * @param size page size
     * @return list of realm DTOs
     */
    List<RealmDTO> findAll(int page, int size);

    /**
     * Update a realm.
     *
     * @param realmDTO the realm with updated fields
     * @return the updated realm
     */
    RealmDTO update(RealmDTO realmDTO);

    /**
     * Enable a realm.
     *
     * @param realmDTO the realm to enable
     * @return the enabled realm
     */
    RealmDTO enable(RealmDTO realmDTO);

    /**
     * Disable a realm.
     *
     * @param realmDTO the realm to disable
     * @return the disabled realm
     */
    RealmDTO disable(RealmDTO realmDTO);

    /**
     * Delete a realm.
     *
     * @param id the realm ID
     */
    void delete(UUID id);

    /**
     * Check if a realm exists and is enabled.
     *
     * @param id the realm ID
     * @return true if realm exists and is enabled
     */
    boolean isActive(UUID id);

    /**
     * Get the count of all realms.
     *
     * @return count of realms
     */
    long count();
}