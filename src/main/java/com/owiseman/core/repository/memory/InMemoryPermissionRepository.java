package com.owiseman.core.repository.memory;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.owiseman.core.domain.Permission;
import com.owiseman.core.exception.ResourceNotFoundException;
import com.owiseman.core.jooq.repository.PermissionRepository;

@Repository
@Profile("dev")
public class InMemoryPermissionRepository implements PermissionRepository {

    private final Map<UUID, Permission> permissions = new ConcurrentHashMap<>();

    @Override
    public Optional<Permission> findById(UUID id) {
        return Optional.ofNullable(permissions.get(id));
    }

    @Override
    public Optional<Permission> findByResourceAction(UUID realmId, String resource, String action) {
        if (realmId == null) {
            throw new IllegalArgumentException("realmId is required");
        }
        if (resource == null || action == null) {
            throw new IllegalArgumentException("resource and action are required");
        }
        return permissions.values().stream()
            .filter(p -> realmId.equals(p.getRealmId()))
            .filter(p -> resource.equals(p.getResource()) && action.equals(p.getAction()))
            .findFirst();
    }

    @Override
    public List<Permission> findAll(UUID realmId, int offset, int limit) {
        return permissions.values().stream()
            .filter(p -> realmId == null || realmId.equals(p.getRealmId()))
            .sorted(Comparator.comparing(Permission::getResource, Comparator.nullsLast(String::compareTo))
                .thenComparing(Permission::getAction, Comparator.nullsLast(String::compareTo)))
            .skip(Math.max(0, offset))
            .limit(Math.max(0, limit))
            .toList();
    }

    @Override
    public Permission create(Permission permission) {
        if (permission.getRealmId() == null) {
            throw new IllegalArgumentException("realmId is required");
        }
        if (permission.getResource() == null || permission.getAction() == null) {
            throw new IllegalArgumentException("resource and action are required");
        }
        if (exists(permission.getRealmId(), permission.getResource(), permission.getAction(), null)) {
            throw new IllegalArgumentException("Permission already exists for realm/resource/action");
        }

        if (permission.getId() == null) {
            permission.setId(UUID.randomUUID());
        }
        if (permission.getCreatedAt() == null) {
            permission.setCreatedAt(LocalDateTime.now());
        }

        permissions.put(permission.getId(), permission);
        return permission;
    }

    @Override
    public Permission update(Permission permission) {
        if (permission.getId() == null) {
            throw new IllegalArgumentException("id is required");
        }
        Permission existing = permissions.get(permission.getId());
        if (existing == null) {
            throw new ResourceNotFoundException("Permission", permission.getId().toString());
        }

        UUID realmId = permission.getRealmId() != null ? permission.getRealmId() : existing.getRealmId();
        String resource = permission.getResource() != null ? permission.getResource() : existing.getResource();
        String action = permission.getAction() != null ? permission.getAction() : existing.getAction();

        if (realmId == null) {
            throw new IllegalArgumentException("realmId is required");
        }
        if (resource == null || action == null) {
            throw new IllegalArgumentException("resource and action are required");
        }
        if (exists(realmId, resource, action, permission.getId())) {
            throw new IllegalArgumentException("Permission already exists for realm/resource/action");
        }

        Permission updated = new Permission();
        updated.setId(permission.getId());
        updated.setRealmId(realmId);
        updated.setResource(resource);
        updated.setAction(action);
        updated.setCreatedAt(existing.getCreatedAt());

        permissions.put(updated.getId(), updated);
        return updated;
    }

    @Override
    public void delete(UUID id) {
        if (!permissions.containsKey(id)) {
            throw new ResourceNotFoundException("Permission", id.toString());
        }
        permissions.remove(id);
    }

    @Override
    public List<Permission> findByPolicy(UUID policyId) {
        return Collections.emptyList();
    }

    @Override
    public long countByRealm(UUID realmId) {
        return permissions.values().stream()
            .filter(p -> realmId != null && realmId.equals(p.getRealmId()))
            .count();
    }

    private boolean exists(UUID realmId, String resource, String action, UUID excludingId) {
        return permissions.values().stream()
            .filter(p -> excludingId == null || !excludingId.equals(p.getId()))
            .anyMatch(p -> realmId.equals(p.getRealmId())
                && resource.equals(p.getResource())
                && action.equals(p.getAction()));
    }
}

