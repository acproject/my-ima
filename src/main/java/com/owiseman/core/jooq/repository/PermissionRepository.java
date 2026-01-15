package com.owiseman.core.jooq.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.owiseman.core.domain.Permission;

public interface PermissionRepository {

    Optional<Permission> findById(UUID id);

    Optional<Permission> findByResourceAction(UUID realmId, String resource, String action);

    List<Permission> findAll(UUID realmId, int offset, int limit);

    Permission create(Permission permission);

    Permission update(Permission permission);

    void delete(UUID id);

    List<Permission> findByPolicy(UUID policyId);

    long countByRealm(UUID realmId);
}
