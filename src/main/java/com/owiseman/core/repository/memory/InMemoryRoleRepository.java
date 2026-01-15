package com.owiseman.core.repository.memory;

import com.owiseman.core.domain.Role;
import com.owiseman.core.exception.ResourceNotFoundException;
import com.owiseman.core.jooq.repository.RoleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of RoleRepository for development and testing.
 */
@Repository
@Profile("dev")
public class InMemoryRoleRepository implements RoleRepository {
    
    private final Map<UUID, Role> roles = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> rolePermissions = new ConcurrentHashMap<>();

    @Override
    public Optional<Role> findById(UUID id) {
        return Optional.ofNullable(roles.get(id));
    }

    @Override
    public Optional<Role> findByName(UUID realmId, String name) {
        return roles.values().stream()
            .filter(r -> r.getName().equals(name))
            .filter(r -> realmId == null || r.getRealmId().equals(realmId))
            .findFirst();
    }

    @Override
    public List<Role> findAll(UUID realmId, int offset, int limit) {
        return roles.values().stream()
            .filter(r -> realmId == null || r.getRealmId().equals(realmId))
            .skip(offset)
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Override
    public Role create(Role role) {
        if (role.getId() == null) {
            role.setId(UUID.randomUUID());
        }
        role.setCreatedAt(LocalDateTime.now());
        roles.put(role.getId(), role);
        rolePermissions.put(role.getId(), new HashSet<>());
        return role;
    }

    @Override
    public Role update(Role role) {
        if (!roles.containsKey(role.getId())) {
            throw new ResourceNotFoundException("Role", role.getId().toString());
        }
        roles.put(role.getId(), role);
        return role;
    }

    @Override
    public void delete(UUID id) {
        if (!roles.containsKey(id)) {
            throw new ResourceNotFoundException("Role", id.toString());
        }
        roles.remove(id);
        rolePermissions.remove(id);
    }

    @Override
    public List<String> findPermissions(UUID roleId) {
        // Simplified - returns permission IDs as strings
        if (!rolePermissions.containsKey(roleId)) {
            return Collections.emptyList();
        }
        return rolePermissions.get(roleId).stream()
            .map(UUID::toString)
            .collect(Collectors.toList());
    }

    @Override
    public void assignPermission(UUID roleId, UUID permissionId) {
        if (!roles.containsKey(roleId)) {
            throw new ResourceNotFoundException("Role", roleId.toString());
        }
        rolePermissions.computeIfAbsent(roleId, k -> new HashSet<>()).add(permissionId);
    }

    @Override
    public void removePermission(UUID roleId, UUID permissionId) {
        if (rolePermissions.containsKey(roleId)) {
            rolePermissions.get(roleId).remove(permissionId);
        }
    }

    /**
     * Get count of roles in a realm.
     */
    @Override
    public long countByRealm(UUID realmId) {
        return roles.values().stream()
            .filter(r -> r.getRealmId().equals(realmId))
            .count();
    }

    /**
     * Clear all roles (for testing).
     */
    public void clear() {
        roles.clear();
        rolePermissions.clear();
    }
}
