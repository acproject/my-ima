package com.owiseman.core.repository.memory;

import com.owiseman.core.domain.Realm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InMemoryRealmRepository.
 */
class InMemoryRealmRepositoryTest {
    
    private InMemoryRealmRepository repository;
    
    @BeforeEach
    void setUp() {
        repository = new InMemoryRealmRepository();
    }
    
    @Test
    void testCreateRealm() {
        Realm realm = new Realm();
        realm.setName("test-realm");
        realm.setEnabled(true);
        
        Realm created = repository.create(realm);
        
        assertNotNull(created.getId());
        assertEquals("test-realm", created.getName());
        assertTrue(created.getEnabled());
        assertNotNull(created.getCreatedAt());
    }
    
    @Test
    void testFindById() {
        Realm realm = repository.create(new Realm());
        realm.setName("test-realm");
        
        Optional<Realm> found = repository.findById(realm.getId());
        
        assertTrue(found.isPresent());
        assertEquals("test-realm", found.get().getName());
    }
    
    @Test
    void testFindByIdNotFound() {
        Optional<Realm> found = repository.findById(UUID.randomUUID());
        assertFalse(found.isPresent());
    }
    
    @Test
    void testFindByName() {
        Realm realm = new Realm();
        realm.setName("my-realm");
        repository.create(realm);
        
        Optional<Realm> found = repository.findByName(null, "my-realm");
        
        assertTrue(found.isPresent());
        assertEquals("my-realm", found.get().getName());
    }
    
    @Test
    void testFindAll() {
        for (int i = 0; i < 5; i++) {
            Realm realm = new Realm();
            realm.setName("realm-" + i);
            repository.create(realm);
        }
        
        List<Realm> all = repository.findAll(null, 0, 10);
        assertEquals(5, all.size());
        
        List<Realm> firstTwo = repository.findAll(null, 0, 2);
        assertEquals(2, firstTwo.size());
    }
    
    @Test
    void testUpdate() {
        Realm realm = repository.create(new Realm());
        realm.setName("original-name");
        
        realm.setName("updated-name");
        Realm updated = repository.update(realm);
        
        assertEquals("updated-name", updated.getName());
        
        Optional<Realm> found = repository.findById(realm.getId());
        assertEquals("updated-name", found.get().getName());
    }
    
    @Test
    void testEnable() {
        Realm realm = repository.create(new Realm());
        realm.setEnabled(false);
        
        Realm enabled = repository.enable(realm.getId());
        
        assertTrue(enabled.getEnabled());
    }
    
    @Test
    void testDisable() {
        Realm realm = repository.create(new Realm());
        realm.setEnabled(true);
        
        Realm disabled = repository.disable(realm.getId());
        
        assertFalse(disabled.getEnabled());
    }
    
    @Test
    void testCount() {
        assertEquals(0, repository.count());
        
        repository.create(new Realm());
        repository.create(new Realm());
        repository.create(new Realm());
        
        assertEquals(3, repository.count());
    }
    
    @Test
    void testClear() {
        repository.create(new Realm());
        repository.create(new Realm());
        
        repository.clear();
        
        assertEquals(0, repository.count());
    }
}
