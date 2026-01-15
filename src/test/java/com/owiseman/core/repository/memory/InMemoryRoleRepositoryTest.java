package com.owiseman.core.repository.memory;

import com.owiseman.core.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InMemoryRoleRepository.
 */
class InMemoryRoleRepositoryTest {
    
    private InMemoryRoleRepository repository;
    private UUID testRealmId;
    
    @BeforeEach
    void setUp() {
        repository = new InMemoryRoleRepository();
        testRealmId = UUID.randomUUID();
    }
    
    @Test
    void testCreateRole() {
        Role role = new Role();
        role.setRealmId(testRealmId);
        role.setName("admin");
        role.setDescription("Administrator role");
        
        Role created = repository.create(role);
        
        assertNotNull(created.getId());
        assertEquals(testRealmId, created.getRealmId());
        assertEquals("admin", created.getName());
        assertEquals("Administrator role", created.getDescription());
        assertNotNull(created.getCreatedAt());
    }
    
    @Test
    void testFindById() {
        Role role = repository.create(new Role());
        role.setName("test-role");
        
        Optional<Role> found = repository.findById(role.getId());
        
        assertTrue(found.isPresent());
        assertEquals("test-role", found.get().getName());
    }
    
    @Test
    void testFindByName() {
        Role role = new Role();
        role.setRealmId(testRealmId);
        role.setName("unique-role");
        repository.create(role);
        
        Optional<Role> found = repository.findByName(testRealmId, "unique-role");
        
        assertTrue(found.isPresent());
        assertEquals("unique-role", found.get().getName());
    }
    
    @Test
    void testFindAll() {
        for (int i = 0; i < 5; i++) {
            Role role = new Role();
            role.setRealmId(testRealmId);
            role.setName("role-" + i);
            repository.create(role);
        }
        
        List<Role> all = repository.findAll(testRealmId, 0, 10);
        assertEquals(5, all.size());
        
        List<Role> firstTwo = repository.findAll(testRealmId, 0, 2);
        assertEquals(2, firstTwo.size());
    }
    
    @Test
    void testUpdate() {
        Role role = repository.create(new Role());
        role.setName("original");
        
        role.setName("updated");
        role.setDescription("Updated description");
        Role updated = repository.update(role);
        
        assertEquals("updated", updated.getName());
        assertEquals("Updated description", updated.getDescription());
    }
    
    @Test
    void testDelete() {
        Role role = repository.create(new Role());
        
        repository.delete(role.getId());
        
        assertFalse(repository.findById(role.getId()).isPresent());
    }
    
    @Test
    void testAssignPermission() {
        Role role = repository.create(new Role());
        UUID permissionId = UUID.randomUUID();
        
        repository.assignPermission(role.getId(), permissionId);
        
        List<String> permissions = repository.findPermissions(role.getId());
        assertTrue(permissions.contains(permissionId.toString()));
    }
    
    @Test
    void testRemovePermission() {
        Role role = repository.create(new Role());
        UUID permissionId = UUID.randomUUID();
        
        repository.assignPermission(role.getId(), permissionId);
        repository.removePermission(role.getId(), permissionId);
        
        List<String> permissions = repository.findPermissions(role.getId());
        assertFalse(permissions.contains(permissionId.toString()));
    }
    
    @Test
    void testCountByRealm() {
        for (int i = 0; i < 5; i++) {
            Role role = new Role();
            role.setRealmId(testRealmId);
            repository.create(role);
        }
        
        UUID otherRealmId = UUID.randomUUID();
        for (int i = 0; i < 2; i++) {
            Role role = new Role();
            role.setRealmId(otherRealmId);
            repository.create(role);
        }
        
        assertEquals(5, repository.countByRealm(testRealmId));
        assertEquals(2, repository.countByRealm(otherRealmId));
    }
    
    @Test
    void testClear() {
        repository.create(new Role());
        repository.create(new Role());
        
        repository.clear();
        
        assertEquals(0, repository.countByRealm(testRealmId));
    }
}
