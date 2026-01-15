package com.owiseman.core.repository.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.owiseman.core.domain.Permission;
import com.owiseman.core.exception.ResourceNotFoundException;

class InMemoryPermissionRepositoryTest {

    private InMemoryPermissionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPermissionRepository();
    }

    @Test
    void testCreateAndFindById() {
        UUID realmId = UUID.randomUUID();
        Permission permission = new Permission();
        permission.setRealmId(realmId);
        permission.setResource("users");
        permission.setAction("read");

        Permission created = repository.create(permission);

        assertNotNull(created.getId());
        assertNotNull(created.getCreatedAt());
        assertEquals(realmId, created.getRealmId());

        Optional<Permission> found = repository.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals("users", found.get().getResource());
        assertEquals("read", found.get().getAction());
    }

    @Test
    void testFindByResourceAction() {
        UUID realmId = UUID.randomUUID();

        Permission permission = new Permission();
        permission.setRealmId(realmId);
        permission.setResource("users");
        permission.setAction("write");
        Permission created = repository.create(permission);

        assertTrue(repository.findByResourceAction(realmId, "users", "write").isPresent());
        assertEquals(created.getId(), repository.findByResourceAction(realmId, "users", "write").get().getId());
        assertFalse(repository.findByResourceAction(realmId, "users", "read").isPresent());
    }

    @Test
    void testFindByResourceActionRequiresRealmId() {
        assertThrows(IllegalArgumentException.class, () -> repository.findByResourceAction(null, "users", "read"));
    }

    @Test
    void testFindAllPaginationAndCountByRealm() {
        UUID realmA = UUID.randomUUID();
        UUID realmB = UUID.randomUUID();

        repository.create(newPermission(realmA, "a", "1"));
        repository.create(newPermission(realmA, "a", "2"));
        repository.create(newPermission(realmA, "b", "1"));
        repository.create(newPermission(realmB, "a", "1"));

        assertEquals(3, repository.countByRealm(realmA));
        assertEquals(1, repository.countByRealm(realmB));

        List<Permission> allRealmA = repository.findAll(realmA, 0, 10);
        assertEquals(3, allRealmA.size());

        List<Permission> firstTwo = repository.findAll(realmA, 0, 2);
        assertEquals(2, firstTwo.size());
    }

    @Test
    void testUpdate() {
        UUID realmId = UUID.randomUUID();
        Permission created = repository.create(newPermission(realmId, "users", "read"));

        Permission update = new Permission();
        update.setId(created.getId());
        update.setRealmId(realmId);
        update.setResource("users");
        update.setAction("write");

        Permission updated = repository.update(update);
        assertEquals("write", updated.getAction());
        assertEquals(created.getCreatedAt(), updated.getCreatedAt());
    }

    @Test
    void testUpdateNotFound() {
        Permission update = new Permission();
        update.setId(UUID.randomUUID());
        update.setRealmId(UUID.randomUUID());
        update.setResource("users");
        update.setAction("read");

        assertThrows(ResourceNotFoundException.class, () -> repository.update(update));
    }

    @Test
    void testDelete() {
        UUID realmId = UUID.randomUUID();
        Permission created = repository.create(newPermission(realmId, "users", "read"));

        repository.delete(created.getId());
        assertFalse(repository.findById(created.getId()).isPresent());
    }

    @Test
    void testDeleteNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> repository.delete(UUID.randomUUID()));
    }

    @Test
    void testCreateEnforcesUniqueness() {
        UUID realmId = UUID.randomUUID();
        repository.create(newPermission(realmId, "users", "read"));
        assertThrows(IllegalArgumentException.class, () -> repository.create(newPermission(realmId, "users", "read")));
    }

    private static Permission newPermission(UUID realmId, String resource, String action) {
        Permission permission = new Permission();
        permission.setRealmId(realmId);
        permission.setResource(resource);
        permission.setAction(action);
        return permission;
    }
}

