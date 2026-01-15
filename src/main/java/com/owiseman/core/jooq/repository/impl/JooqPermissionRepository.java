package com.owiseman.core.jooq.repository.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.owiseman.core.domain.Permission;
import com.owiseman.core.exception.ResourceNotFoundException;
import com.owiseman.core.jooq.repository.PermissionRepository;

import static com.owiseman.core.jooq.generated.tables.ImaPermission.IMA_PERMISSION;
import static com.owiseman.core.jooq.generated.tables.ImaPermissionPolicy.IMA_PERMISSION_POLICY;

@Repository
@Profile("prod")
public class JooqPermissionRepository implements PermissionRepository {

    private final DSLContext dsl;

    public JooqPermissionRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<Permission> findById(UUID id) {
        return dsl.select(IMA_PERMISSION.ID, IMA_PERMISSION.REALM_ID, IMA_PERMISSION.RESOURCE, IMA_PERMISSION.ACTION, IMA_PERMISSION.CREATED_AT)
            .from(IMA_PERMISSION)
            .where(IMA_PERMISSION.ID.eq(id))
            .fetchOptional(this::mapPermission);
    }

    @Override
    public Optional<Permission> findByResourceAction(UUID realmId, String resource, String action) {
        if (realmId == null) {
            throw new IllegalArgumentException("realmId is required");
        }
        return dsl.select(IMA_PERMISSION.ID, IMA_PERMISSION.REALM_ID, IMA_PERMISSION.RESOURCE, IMA_PERMISSION.ACTION, IMA_PERMISSION.CREATED_AT)
            .from(IMA_PERMISSION)
            .where(IMA_PERMISSION.REALM_ID.eq(realmId).and(IMA_PERMISSION.RESOURCE.eq(resource)).and(IMA_PERMISSION.ACTION.eq(action)))
            .fetchOptional(this::mapPermission);
    }

    @Override
    public List<Permission> findAll(UUID realmId, int offset, int limit) {
        Condition condition = realmId == null ? DSL.noCondition() : IMA_PERMISSION.REALM_ID.eq(realmId);
        return dsl.select(IMA_PERMISSION.ID, IMA_PERMISSION.REALM_ID, IMA_PERMISSION.RESOURCE, IMA_PERMISSION.ACTION, IMA_PERMISSION.CREATED_AT)
            .from(IMA_PERMISSION)
            .where(condition)
            .orderBy(IMA_PERMISSION.RESOURCE.asc(), IMA_PERMISSION.ACTION.asc())
            .offset(Math.max(0, offset))
            .limit(Math.max(0, limit))
            .fetch(this::mapPermission);
    }

    @Override
    public Permission create(Permission permission) {
        if (permission.getRealmId() == null) {
            throw new IllegalArgumentException("realmId is required");
        }
        if (permission.getResource() == null || permission.getAction() == null) {
            throw new IllegalArgumentException("resource and action are required");
        }

        String name = permission.getResource() + ":" + permission.getAction();

        Record record = dsl.insertInto(IMA_PERMISSION)
            .set(IMA_PERMISSION.REALM_ID, permission.getRealmId())
            .set(IMA_PERMISSION.NAME, name)
            .set(IMA_PERMISSION.RESOURCE, permission.getResource())
            .set(IMA_PERMISSION.ACTION, permission.getAction())
            .returning(IMA_PERMISSION.ID, IMA_PERMISSION.REALM_ID, IMA_PERMISSION.RESOURCE, IMA_PERMISSION.ACTION, IMA_PERMISSION.CREATED_AT)
            .fetchOne();

        if (record == null) {
            throw new IllegalStateException("Failed to create permission");
        }

        return mapPermission(record);
    }

    @Override
    public Permission update(Permission permission) {
        if (permission.getId() == null) {
            throw new IllegalArgumentException("id is required");
        }
        if (permission.getResource() == null || permission.getAction() == null) {
            throw new IllegalArgumentException("resource and action are required");
        }

        String name = permission.getResource() + ":" + permission.getAction();

        Record record = dsl.update(IMA_PERMISSION)
            .set(IMA_PERMISSION.NAME, name)
            .set(IMA_PERMISSION.RESOURCE, permission.getResource())
            .set(IMA_PERMISSION.ACTION, permission.getAction())
            .where(IMA_PERMISSION.ID.eq(permission.getId()))
            .returning(IMA_PERMISSION.ID, IMA_PERMISSION.REALM_ID, IMA_PERMISSION.RESOURCE, IMA_PERMISSION.ACTION, IMA_PERMISSION.CREATED_AT)
            .fetchOne();

        if (record == null) {
            throw new ResourceNotFoundException("Permission", permission.getId());
        }

        return mapPermission(record);
    }

    @Override
    public void delete(UUID id) {
        int deleted = dsl.deleteFrom(IMA_PERMISSION)
            .where(IMA_PERMISSION.ID.eq(id))
            .execute();
        if (deleted == 0) {
            throw new ResourceNotFoundException("Permission", id);
        }
    }

    @Override
    public List<Permission> findByPolicy(UUID policyId) {
        return dsl.select(IMA_PERMISSION.ID, IMA_PERMISSION.REALM_ID, IMA_PERMISSION.RESOURCE, IMA_PERMISSION.ACTION, IMA_PERMISSION.CREATED_AT)
            .from(IMA_PERMISSION)
            .join(IMA_PERMISSION_POLICY).on(IMA_PERMISSION_POLICY.PERMISSION_ID.eq(IMA_PERMISSION.ID))
            .where(IMA_PERMISSION_POLICY.POLICY_ID.eq(policyId))
            .orderBy(IMA_PERMISSION.RESOURCE.asc(), IMA_PERMISSION.ACTION.asc())
            .fetch(this::mapPermission);
    }

    @Override
    public long countByRealm(UUID realmId) {
        Long count = dsl.selectCount()
            .from(IMA_PERMISSION)
            .where(IMA_PERMISSION.REALM_ID.eq(realmId))
            .fetchOne(0, Long.class);
        return count != null ? count : 0L;
    }

    private Permission mapPermission(Record record) {
        Permission permission = new Permission();
        permission.setId(record.get(IMA_PERMISSION.ID));
        permission.setRealmId(record.get(IMA_PERMISSION.REALM_ID));
        permission.setResource(record.get(IMA_PERMISSION.RESOURCE));
        permission.setAction(record.get(IMA_PERMISSION.ACTION));
        OffsetDateTime createdAt = record.get(IMA_PERMISSION.CREATED_AT);
        permission.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        return permission;
    }
}

