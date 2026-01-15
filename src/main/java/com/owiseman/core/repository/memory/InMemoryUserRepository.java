package com.owiseman.core.repository.memory;

import com.owiseman.core.domain.User;
import com.owiseman.core.exception.ResourceNotFoundException;
import com.owiseman.core.jooq.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of UserRepository for development and testing.
 * Provides fast CRUD operations without database dependency.
 */
@Repository
@Profile("dev")
public class InMemoryUserRepository implements UserRepository {
    
    private final Map<UUID, User> users = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> userRoles = new ConcurrentHashMap<>();

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByUsername(UUID realmId, String username) {
        return users.values().stream()
            .filter(u -> u.getUsername().equals(username))
            .filter(u -> realmId == null || u.getRealmId().equals(realmId))
            .findFirst();
    }

    @Override
    public Optional<User> findByEmail(UUID realmId, String email) {
        return users.values().stream()
            .filter(u -> u.getEmail() != null && u.getEmail().equals(email))
            .filter(u -> realmId == null || u.getRealmId().equals(realmId))
            .findFirst();
    }

    @Override
    public List<User> findAll(UUID realmId, int offset, int limit) {
        return users.values().stream()
            .filter(u -> realmId == null || u.getRealmId().equals(realmId))
            .skip(offset)
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Override
    public User create(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        user.setCreatedAt(LocalDateTime.now());
        if (user.getEnabled() == null) {
            user.setEnabled(true);
        }
        users.put(user.getId(), user);
        userRoles.put(user.getId(), new HashSet<>());
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ResourceNotFoundException("User", user.getId().toString());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(UUID id) {
        if (!users.containsKey(id)) {
            throw new ResourceNotFoundException("User", id.toString());
        }
        users.remove(id);
        userRoles.remove(id);
    }

    @Override
    public List<String> findPermissions(UUID userId) {
        // Simplified permission model - returns empty list for in-memory
        // In real implementation, this would query role -> permission relationships
        return Collections.emptyList();
    }

    @Override
    public void assignRole(UUID userId, UUID roleId) {
        if (!users.containsKey(userId)) {
            throw new ResourceNotFoundException("User", userId.toString());
        }
        userRoles.computeIfAbsent(userId, k -> new HashSet<>()).add(roleId);
    }

    @Override
    public void removeRole(UUID userId, UUID roleId) {
        if (userRoles.containsKey(userId)) {
            userRoles.get(userId).remove(roleId);
        }
    }

    @Override
    public List<String> findUserRoles(UUID userId) {
        // Simplified - returns role IDs as strings
        if (!userRoles.containsKey(userId)) {
            return Collections.emptyList();
        }
        return userRoles.get(userId).stream()
            .map(UUID::toString)
            .collect(Collectors.toList());
    }

    /**
     * Get count of users in a realm.
     */
    @Override
    public long countByRealm(UUID realmId) {
        return users.values().stream()
            .filter(u -> u.getRealmId().equals(realmId))
            .count();
    }

    /**
     * Clear all users (for testing).
     */
    public void clear() {
        users.clear();
        userRoles.clear();
    }

    /**
     * Save a user (upsert).
     */
    public User save(User user) {
        if (user.getId() == null) {
            return create(user);
        } else {
            return update(user);
        }
    }
}
