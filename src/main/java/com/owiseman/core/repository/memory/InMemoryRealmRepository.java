package com.owiseman.core.repository.memory;

import com.owiseman.core.domain.Realm;
import com.owiseman.core.exception.ResourceNotFoundException;
import com.owiseman.core.jooq.repository.RealmRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of RealmRepository for development and testing.
 * Provides fast CRUD operations without database dependency.
 */
@Repository
@Profile("dev")
public class InMemoryRealmRepository implements RealmRepository {
    
    private final Map<UUID, Realm> realms = new ConcurrentHashMap<>();

    @Override
    public Optional<Realm> findById(UUID id) {
        return Optional.ofNullable(realms.get(id));
    }

    @Override
    public Optional<Realm> findByName(UUID realmId, String name) {
        return realms.values().stream()
            .filter(r -> r.getName().equals(name))
            .findFirst();
    }

    @Override
    public List<Realm> findAll(UUID realmId, int offset, int limit) {
        return realms.values().stream()
            .skip(offset)
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Override
    public Realm create(Realm realm) {
        if (realm.getId() == null) {
            realm.setId(UUID.randomUUID());
        }
        realm.setCreatedAt(LocalDateTime.now());
        if (realm.getEnabled() == null) {
            realm.setEnabled(true);
        }
        realms.put(realm.getId(), realm);
        return realm;
    }

    @Override
    public Realm update(Realm realm) {
        if (!realms.containsKey(realm.getId())) {
            throw new ResourceNotFoundException("Realm", realm.getId().toString());
        }
        realms.put(realm.getId(), realm);
        return realm;
    }

    @Override
    public Realm enable(UUID id) {
        Realm realm = realms.get(id);
        if (realm == null) {
            throw new ResourceNotFoundException("Realm", id.toString());
        }
        realm.setEnabled(true);
        return realm;
    }

    @Override
    public Realm disable(UUID id) {
        Realm realm = realms.get(id);
        if (realm == null) {
            throw new ResourceNotFoundException("Realm", id.toString());
        }
        realm.setEnabled(false);
        return realm;
    }

    @Override
    public void delete(UUID id) {
        if (!realms.containsKey(id)) {
            throw new ResourceNotFoundException("Realm", id.toString());
        }
        realms.remove(id);
    }

    /**
     * Get count of all realms.
     */
    @Override
    public long count() {
        return realms.size();
    }

    /**
     * Get count of enabled realms.
     */
    public long countEnabled() {
        return realms.values().stream()
            .filter(r -> Boolean.TRUE.equals(r.getEnabled()))
            .count();
    }

    /**
     * Clear all realms (for testing).
     */
    public void clear() {
        realms.clear();
    }

    /**
     * Save a realm (upsert).
     */
    public Realm save(Realm realm) {
        if (realm.getId() == null) {
            return create(realm);
        } else {
            return update(realm);
        }
    }
}
