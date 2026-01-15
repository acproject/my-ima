package com.owiseman.core.jooq.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.owiseman.core.domain.Realm;
import com.owiseman.core.domain.Role;
import com.owiseman.core.domain.User;
import com.owiseman.core.domain.Permission;
import com.owiseman.core.jooq.repository.PermissionRepository;
import com.owiseman.core.jooq.repository.RealmRepository;
import com.owiseman.core.jooq.repository.RoleRepository;
import com.owiseman.core.jooq.repository.UserRepository;

import static com.owiseman.core.jooq.generated.tables.ImaPermission.IMA_PERMISSION;

@Testcontainers
@SpringBootTest
@ActiveProfiles("prod")
@Transactional
class JooqRepositoriesTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("my_ima_test")
        .withUsername("postgres")
        .withPassword("postgres");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jooq.sql-dialect", () -> "POSTGRES");
        registry.add("spring.liquibase.enabled", () -> true);
        registry.add("spring.liquibase.change-log", () -> "classpath:db/migration/master.xml");
    }

    @Autowired
    RealmRepository realmRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    DSLContext dsl;

    @Test
    void realmCrudAndCount() {
        Realm realm = new Realm();
        realm.setName("realm-" + UUID.randomUUID());
        realm.setEnabled(true);

        Realm created = realmRepository.create(realm);
        assertNotNull(created.getId());
        assertTrue(realmRepository.findById(created.getId()).isPresent());
        assertTrue(realmRepository.count() >= 1);
    }

    @Test
    void permissionCrudAndCountByRealm() {
        Realm realm = new Realm();
        realm.setName("realm-" + UUID.randomUUID());
        realm.setEnabled(true);
        realm = realmRepository.create(realm);

        Permission permission = new Permission();
        permission.setRealmId(realm.getId());
        permission.setResource("users");
        permission.setAction("read");

        Permission created = permissionRepository.create(permission);
        assertNotNull(created.getId());

        assertTrue(permissionRepository.findById(created.getId()).isPresent());
        assertTrue(permissionRepository.findByResourceAction(realm.getId(), "users", "read").isPresent());
        assertEquals(1L, permissionRepository.countByRealm(realm.getId()));

        created.setAction("write");
        Permission updated = permissionRepository.update(created);
        assertEquals("write", updated.getAction());

        permissionRepository.delete(created.getId());
        assertTrue(permissionRepository.findById(created.getId()).isEmpty());
        assertEquals(0L, permissionRepository.countByRealm(realm.getId()));
    }

    @Test
    void rolePermissionAssignment() {
        Realm realm = new Realm();
        realm.setName("realm-" + UUID.randomUUID());
        realm.setEnabled(true);
        realm = realmRepository.create(realm);

        Role role = new Role();
        role.setRealmId(realm.getId());
        role.setName("role-" + UUID.randomUUID());
        role = roleRepository.create(role);

        UUID permissionId = insertPermission(realm.getId(), "users", "read");

        roleRepository.assignPermission(role.getId(), permissionId);
        assertTrue(roleRepository.findPermissions(role.getId()).contains(permissionId.toString()));
        assertEquals(1L, roleRepository.countByRealm(realm.getId()));
    }

    @Test
    void userRoleAndPermissionResolution() {
        Realm realm = new Realm();
        realm.setName("realm-" + UUID.randomUUID());
        realm.setEnabled(true);
        realm = realmRepository.create(realm);

        Role role = new Role();
        role.setRealmId(realm.getId());
        role.setName("role-" + UUID.randomUUID());
        role = roleRepository.create(role);

        UUID permissionId = insertPermission(realm.getId(), "users", "write");
        roleRepository.assignPermission(role.getId(), permissionId);

        User user = new User();
        user.setRealmId(realm.getId());
        user.setUsername("user-" + UUID.randomUUID());
        user.setEmail("email-" + UUID.randomUUID() + "@example.com");
        user.setPasswordHash("hash");
        user.setEnabled(true);
        user = userRepository.create(user);

        userRepository.assignRole(user.getId(), role.getId());

        assertTrue(userRepository.findUserRoles(user.getId()).contains(role.getId().toString()));
        assertTrue(userRepository.findPermissions(user.getId()).contains("users:write"));
        assertEquals(1L, userRepository.countByRealm(realm.getId()));
    }

    private UUID insertPermission(UUID realmId, String resource, String action) {
        return dsl.insertInto(IMA_PERMISSION)
            .set(IMA_PERMISSION.REALM_ID, realmId)
            .set(IMA_PERMISSION.NAME, "perm-" + resource + "-" + action + "-" + UUID.randomUUID())
            .set(IMA_PERMISSION.RESOURCE, resource)
            .set(IMA_PERMISSION.ACTION, action)
            .returning(IMA_PERMISSION.ID)
            .fetchOne(IMA_PERMISSION.ID);
    }
}
