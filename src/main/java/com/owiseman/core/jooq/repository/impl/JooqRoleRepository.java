package com.owiseman.core.jooq.repository.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.owiseman.core.domain.Role;
import com.owiseman.core.exception.ResourceNotFoundException;
import com.owiseman.core.jooq.repository.RoleRepository;

import static com.owiseman.core.jooq.generated.tables.ImaRole.IMA_ROLE;
import static com.owiseman.core.jooq.generated.tables.ImaRolePermission.IMA_ROLE_PERMISSION;

@Repository
@Profile("prod")
public class JooqRoleRepository implements RoleRepository {
    private final DSLContext dsl;

    public JooqRoleRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return dsl.select(IMA_ROLE.ID, IMA_ROLE.REALM_ID, IMA_ROLE.NAME, IMA_ROLE.DESCRIPTION, IMA_ROLE.CREATED_AT)
            .from(IMA_ROLE)
            .where(IMA_ROLE.ID.eq(id))
            .fetchOptional(this::mapRole);
    }

    @Override
    public Optional<Role> findByName(UUID realmId, String name) {
        return dsl.select(IMA_ROLE.ID, IMA_ROLE.REALM_ID, IMA_ROLE.NAME, IMA_ROLE.DESCRIPTION, IMA_ROLE.CREATED_AT)
            .from(IMA_ROLE)
            .where(IMA_ROLE.REALM_ID.eq(realmId).and(IMA_ROLE.NAME.eq(name)))
            .fetchOptional(this::mapRole);
    }

    @Override
    public List<Role> findAll(UUID realmId, int offset, int limit) {
        var condition = realmId == null ? DSL.noCondition() : IMA_ROLE.REALM_ID.eq(realmId);
        return dsl.select(IMA_ROLE.ID, IMA_ROLE.REALM_ID, IMA_ROLE.NAME, IMA_ROLE.DESCRIPTION, IMA_ROLE.CREATED_AT)
            .from(IMA_ROLE)
            .where(condition)
            .orderBy(IMA_ROLE.CREATED_AT.desc())
            .offset(Math.max(0, offset))
            .limit(Math.max(0, limit))
            .fetch(this::mapRole);
    }

    @Override
    public Role create(Role role) {
        Record record = dsl.insertInto(IMA_ROLE)
            .set(IMA_ROLE.REALM_ID, role.getRealmId())
            .set(IMA_ROLE.NAME, role.getName())
            .set(IMA_ROLE.DESCRIPTION, role.getDescription())
            .returning(IMA_ROLE.ID, IMA_ROLE.REALM_ID, IMA_ROLE.NAME, IMA_ROLE.DESCRIPTION, IMA_ROLE.CREATED_AT)
            .fetchOne();

        if (record == null) {
            throw new IllegalStateException("Failed to create role");
        }

        return mapRole(record);
    }

    @Override
    public Role update(Role role) {
        Record record = dsl.update(IMA_ROLE)
            .set(IMA_ROLE.NAME, role.getName())
            .set(IMA_ROLE.DESCRIPTION, role.getDescription())
            .where(IMA_ROLE.ID.eq(role.getId()))
            .returning(IMA_ROLE.ID, IMA_ROLE.REALM_ID, IMA_ROLE.NAME, IMA_ROLE.DESCRIPTION, IMA_ROLE.CREATED_AT)
            .fetchOne();

        if (record == null) {
            throw new ResourceNotFoundException("Role", role.getId());
        }

        return mapRole(record);
    }

    @Override
    public void delete(UUID id) {
        int deleted = dsl.deleteFrom(IMA_ROLE)
            .where(IMA_ROLE.ID.eq(id))
            .execute();
        if (deleted == 0) {
            throw new ResourceNotFoundException("Role", id);
        }
    }

    @Override
    public List<String> findPermissions(UUID roleId) {
        return dsl.select(IMA_ROLE_PERMISSION.PERMISSION_ID)
            .from(IMA_ROLE_PERMISSION)
            .where(IMA_ROLE_PERMISSION.ROLE_ID.eq(roleId))
            .fetch(IMA_ROLE_PERMISSION.PERMISSION_ID)
            .stream()
            .map(UUID::toString)
            .toList();
    }

    @Override
    public void assignPermission(UUID roleId, UUID permissionId) {
        dsl.insertInto(IMA_ROLE_PERMISSION)
            .set(IMA_ROLE_PERMISSION.ROLE_ID, roleId)
            .set(IMA_ROLE_PERMISSION.PERMISSION_ID, permissionId)
            .onConflict(IMA_ROLE_PERMISSION.ROLE_ID, IMA_ROLE_PERMISSION.PERMISSION_ID)
            .doNothing()
            .execute();
    }

    @Override
    public void removePermission(UUID roleId, UUID permissionId) {
        dsl.deleteFrom(IMA_ROLE_PERMISSION)
            .where(IMA_ROLE_PERMISSION.ROLE_ID.eq(roleId).and(IMA_ROLE_PERMISSION.PERMISSION_ID.eq(permissionId)))
            .execute();
    }

    @Override
    public long countByRealm(UUID realmId) {
        Long count = dsl.selectCount()
            .from(IMA_ROLE)
            .where(IMA_ROLE.REALM_ID.eq(realmId))
            .fetchOne(0, Long.class);
        return count != null ? count : 0L;
    }

    private Role mapRole(Record record) {
        Role role = new Role();
        role.setId(record.get(IMA_ROLE.ID));
        role.setRealmId(record.get(IMA_ROLE.REALM_ID));
        role.setName(record.get(IMA_ROLE.NAME));
        role.setDescription(record.get(IMA_ROLE.DESCRIPTION));
        OffsetDateTime createdAt = record.get(IMA_ROLE.CREATED_AT);
        role.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        return role;
    }
}
