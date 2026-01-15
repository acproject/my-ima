package com.owiseman.core.jooq.repository.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.owiseman.core.domain.User;
import com.owiseman.core.exception.ResourceNotFoundException;
import com.owiseman.core.jooq.repository.UserRepository;

import static com.owiseman.core.jooq.generated.tables.ImaPermission.IMA_PERMISSION;
import static com.owiseman.core.jooq.generated.tables.ImaRolePermission.IMA_ROLE_PERMISSION;
import static com.owiseman.core.jooq.generated.tables.ImaUser.IMA_USER;
import static com.owiseman.core.jooq.generated.tables.ImaUserRole.IMA_USER_ROLE;

@Repository
@Profile("prod")
public class JooqUserRepository implements UserRepository {
    private final DSLContext dsl;

    public JooqUserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return dsl.select(IMA_USER.ID, IMA_USER.REALM_ID, IMA_USER.USERNAME, IMA_USER.EMAIL, IMA_USER.PASSWORD_HASH, IMA_USER.FIRST_NAME, IMA_USER.LAST_NAME, IMA_USER.ENABLED, IMA_USER.CREATED_AT)
            .from(IMA_USER)
            .where(IMA_USER.ID.eq(id))
            .fetchOptional(this::mapUser);
    }

    @Override
    public Optional<User> findByUsername(UUID realmId, String username) {
        Condition condition = IMA_USER.USERNAME.eq(username);
        if (realmId != null) {
            condition = condition.and(IMA_USER.REALM_ID.eq(realmId));
        }
        return dsl.select(IMA_USER.ID, IMA_USER.REALM_ID, IMA_USER.USERNAME, IMA_USER.EMAIL, IMA_USER.PASSWORD_HASH, IMA_USER.FIRST_NAME, IMA_USER.LAST_NAME, IMA_USER.ENABLED, IMA_USER.CREATED_AT)
            .from(IMA_USER)
            .where(condition)
            .orderBy(IMA_USER.CREATED_AT.desc())
            .fetchOptional(this::mapUser);
    }

    @Override
    public Optional<User> findByEmail(UUID realmId, String email) {
        Condition condition = IMA_USER.EMAIL.eq(email);
        if (realmId != null) {
            condition = condition.and(IMA_USER.REALM_ID.eq(realmId));
        }
        return dsl.select(IMA_USER.ID, IMA_USER.REALM_ID, IMA_USER.USERNAME, IMA_USER.EMAIL, IMA_USER.PASSWORD_HASH, IMA_USER.FIRST_NAME, IMA_USER.LAST_NAME, IMA_USER.ENABLED, IMA_USER.CREATED_AT)
            .from(IMA_USER)
            .where(condition)
            .orderBy(IMA_USER.CREATED_AT.desc())
            .fetchOptional(this::mapUser);
    }

    @Override
    public List<User> findAll(UUID realmId, int offset, int limit) {
        Condition condition = realmId == null ? DSL.noCondition() : IMA_USER.REALM_ID.eq(realmId);
        return dsl.select(IMA_USER.ID, IMA_USER.REALM_ID, IMA_USER.USERNAME, IMA_USER.EMAIL, IMA_USER.PASSWORD_HASH, IMA_USER.FIRST_NAME, IMA_USER.LAST_NAME, IMA_USER.ENABLED, IMA_USER.CREATED_AT)
            .from(IMA_USER)
            .where(condition)
            .orderBy(IMA_USER.CREATED_AT.desc())
            .offset(Math.max(0, offset))
            .limit(Math.max(0, limit))
            .fetch(this::mapUser);
    }

    @Override
    public User create(User user) {
        if (user.getRealmId() == null) {
            throw new IllegalArgumentException("realmId is required");
        }
        if (user.getUsername() == null || user.getEmail() == null || user.getPasswordHash() == null) {
            throw new IllegalArgumentException("username, email, and passwordHash are required");
        }

        Record record = dsl.insertInto(IMA_USER)
            .set(IMA_USER.REALM_ID, user.getRealmId())
            .set(IMA_USER.USERNAME, user.getUsername())
            .set(IMA_USER.EMAIL, user.getEmail())
            .set(IMA_USER.PASSWORD_HASH, user.getPasswordHash())
            .set(IMA_USER.FIRST_NAME, user.getFirstName())
            .set(IMA_USER.LAST_NAME, user.getLastName())
            .set(IMA_USER.ENABLED, user.getEnabled() != null ? user.getEnabled() : true)
            .returning(IMA_USER.ID, IMA_USER.REALM_ID, IMA_USER.USERNAME, IMA_USER.EMAIL, IMA_USER.PASSWORD_HASH, IMA_USER.FIRST_NAME, IMA_USER.LAST_NAME, IMA_USER.ENABLED, IMA_USER.CREATED_AT)
            .fetchOne();

        if (record == null) {
            throw new IllegalStateException("Failed to create user");
        }

        return mapUser(record);
    }

    @Override
    public User update(User user) {
        Record record = dsl.update(IMA_USER)
            .set(IMA_USER.USERNAME, user.getUsername())
            .set(IMA_USER.EMAIL, user.getEmail())
            .set(IMA_USER.FIRST_NAME, user.getFirstName())
            .set(IMA_USER.LAST_NAME, user.getLastName())
            .set(IMA_USER.ENABLED, user.getEnabled())
            .set(IMA_USER.PASSWORD_HASH, user.getPasswordHash())
            .where(IMA_USER.ID.eq(user.getId()))
            .returning(IMA_USER.ID, IMA_USER.REALM_ID, IMA_USER.USERNAME, IMA_USER.EMAIL, IMA_USER.PASSWORD_HASH, IMA_USER.FIRST_NAME, IMA_USER.LAST_NAME, IMA_USER.ENABLED, IMA_USER.CREATED_AT)
            .fetchOne();

        if (record == null) {
            throw new ResourceNotFoundException("User", user.getId());
        }

        return mapUser(record);
    }

    @Override
    public void delete(UUID id) {
        int deleted = dsl.deleteFrom(IMA_USER)
            .where(IMA_USER.ID.eq(id))
            .execute();
        if (deleted == 0) {
            throw new ResourceNotFoundException("User", id);
        }
    }

    @Override
    public List<String> findPermissions(UUID userId) {
        Field<String> identifier = DSL.concat(IMA_PERMISSION.RESOURCE, DSL.inline(":"), IMA_PERMISSION.ACTION);
        return dsl.selectDistinct(identifier)
            .from(IMA_USER_ROLE)
            .join(IMA_ROLE_PERMISSION).on(IMA_USER_ROLE.ROLE_ID.eq(IMA_ROLE_PERMISSION.ROLE_ID))
            .join(IMA_PERMISSION).on(IMA_ROLE_PERMISSION.PERMISSION_ID.eq(IMA_PERMISSION.ID))
            .where(IMA_USER_ROLE.USER_ID.eq(userId))
            .fetch(identifier);
    }

    @Override
    public void assignRole(UUID userId, UUID roleId) {
        dsl.insertInto(IMA_USER_ROLE)
            .set(IMA_USER_ROLE.USER_ID, userId)
            .set(IMA_USER_ROLE.ROLE_ID, roleId)
            .onConflict(IMA_USER_ROLE.USER_ID, IMA_USER_ROLE.ROLE_ID)
            .doNothing()
            .execute();
    }

    @Override
    public void removeRole(UUID userId, UUID roleId) {
        dsl.deleteFrom(IMA_USER_ROLE)
            .where(IMA_USER_ROLE.USER_ID.eq(userId).and(IMA_USER_ROLE.ROLE_ID.eq(roleId)))
            .execute();
    }

    @Override
    public List<String> findUserRoles(UUID userId) {
        return dsl.select(IMA_USER_ROLE.ROLE_ID)
            .from(IMA_USER_ROLE)
            .where(IMA_USER_ROLE.USER_ID.eq(userId))
            .fetch(IMA_USER_ROLE.ROLE_ID)
            .stream()
            .map(UUID::toString)
            .toList();
    }

    @Override
    public long countByRealm(UUID realmId) {
        Long count = dsl.selectCount()
            .from(IMA_USER)
            .where(IMA_USER.REALM_ID.eq(realmId))
            .fetchOne(0, Long.class);
        return count != null ? count : 0L;
    }

    private User mapUser(Record record) {
        User user = new User();
        user.setId(record.get(IMA_USER.ID));
        user.setRealmId(record.get(IMA_USER.REALM_ID));
        user.setUsername(record.get(IMA_USER.USERNAME));
        user.setEmail(record.get(IMA_USER.EMAIL));
        user.setPasswordHash(record.get(IMA_USER.PASSWORD_HASH));
        user.setFirstName(record.get(IMA_USER.FIRST_NAME));
        user.setLastName(record.get(IMA_USER.LAST_NAME));
        user.setEnabled(record.get(IMA_USER.ENABLED));
        OffsetDateTime createdAt = record.get(IMA_USER.CREATED_AT);
        user.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        return user;
    }
}
