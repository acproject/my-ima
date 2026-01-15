package com.owiseman.core.repository.memory;

import com.owiseman.core.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InMemoryUserRepository.
 */
class InMemoryUserRepositoryTest {
    
    private InMemoryUserRepository repository;
    private UUID testRealmId;
    
    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        testRealmId = UUID.randomUUID();
    }
    
    @Test
    void testCreateUser() {
        User user = new User();
        user.setRealmId(testRealmId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        
        User created = repository.create(user);
        
        assertNotNull(created.getId());
        assertEquals(testRealmId, created.getRealmId());
        assertEquals("testuser", created.getUsername());
        assertEquals("test@example.com", created.getEmail());
        assertTrue(created.getEnabled());
        assertNotNull(created.getCreatedAt());
    }
    
    @Test
    void testFindById() {
        User user = repository.create(new User());
        user.setUsername("testuser");
        
        Optional<User> found = repository.findById(user.getId());
        
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }
    
    @Test
    void testFindByUsername() {
        User user = new User();
        user.setRealmId(testRealmId);
        user.setUsername("uniqueuser");
        repository.create(user);
        
        Optional<User> found = repository.findByUsername(testRealmId, "uniqueuser");
        
        assertTrue(found.isPresent());
        assertEquals("uniqueuser", found.get().getUsername());
    }
    
    @Test
    void testFindByEmail() {
        User user = new User();
        user.setRealmId(testRealmId);
        user.setEmail("unique@example.com");
        repository.create(user);
        
        Optional<User> found = repository.findByEmail(testRealmId, "unique@example.com");
        
        assertTrue(found.isPresent());
        assertEquals("unique@example.com", found.get().getEmail());
    }
    
    @Test
    void testFindAllByRealm() {
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setRealmId(testRealmId);
            user.setUsername("user-" + i);
            repository.create(user);
        }
        
        // Create users in different realm
        UUID otherRealmId = UUID.randomUUID();
        for (int i = 0; i < 3; i++) {
            User user = new User();
            user.setRealmId(otherRealmId);
            user.setUsername("other-" + i);
            repository.create(user);
        }
        
        List<User> realmUsers = repository.findAll(testRealmId, 0, 10);
        assertEquals(5, realmUsers.size());
    }
    
    @Test
    void testUpdate() {
        User user = repository.create(new User());
        user.setUsername("original");
        
        user.setUsername("updated");
        User updated = repository.update(user);
        
        assertEquals("updated", updated.getUsername());
        
        Optional<User> found = repository.findById(user.getId());
        assertEquals("updated", found.get().getUsername());
    }
    
    @Test
    void testDelete() {
        User user = repository.create(new User());
        
        repository.delete(user.getId());
        
        assertFalse(repository.findById(user.getId()).isPresent());
    }
    
    @Test
    void testAssignRole() {
        User user = repository.create(new User());
        UUID roleId = UUID.randomUUID();
        
        repository.assignRole(user.getId(), roleId);
        
        List<String> roles = repository.findUserRoles(user.getId());
        assertTrue(roles.contains(roleId.toString()));
    }
    
    @Test
    void testRemoveRole() {
        User user = repository.create(new User());
        UUID roleId = UUID.randomUUID();
        
        repository.assignRole(user.getId(), roleId);
        repository.removeRole(user.getId(), roleId);
        
        List<String> roles = repository.findUserRoles(user.getId());
        assertFalse(roles.contains(roleId.toString()));
    }
    
    @Test
    void testCountByRealm() {
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setRealmId(testRealmId);
            repository.create(user);
        }
        
        UUID otherRealmId = UUID.randomUUID();
        for (int i = 0; i < 2; i++) {
            User user = new User();
            user.setRealmId(otherRealmId);
            repository.create(user);
        }
        
        assertEquals(5, repository.countByRealm(testRealmId));
        assertEquals(2, repository.countByRealm(otherRealmId));
    }
    
    @Test
    void testClear() {
        repository.create(new User());
        repository.create(new User());
        
        repository.clear();
        
        assertEquals(0, repository.countByRealm(testRealmId));
    }
}
