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

import com.owiseman.core.domain.Realm;
import com.owiseman.core.exception.ResourceNotFoundException;
import com.owiseman.core.jooq.repository.RealmRepository;

import static com.owiseman.core.jooq.generated.tables.ImaRealm.IMA_REALM;

@Repository
@Profile("prod")
public class JooqRealmRepository implements RealmRepository {
    private final DSLContext dsl;

    public JooqRealmRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<Realm> findById(UUID id) {
        return dsl.select(IMA_REALM.ID, IMA_REALM.NAME, IMA_REALM.ENABLED, IMA_REALM.CREATED_AT)
            .from(IMA_REALM)
            .where(IMA_REALM.ID.eq(id))
            .fetchOptional(this::mapRealm);
    }

    @Override
    public Optional<Realm> findByName(UUID realmId, String name) {
        return dsl.select(IMA_REALM.ID, IMA_REALM.NAME, IMA_REALM.ENABLED, IMA_REALM.CREATED_AT)
            .from(IMA_REALM)
            .where(IMA_REALM.NAME.eq(name))
            .fetchOptional(this::mapRealm);
    }

    @Override
    public List<Realm> findAll(UUID realmId, int offset, int limit) {
        return dsl.select(IMA_REALM.ID, IMA_REALM.NAME, IMA_REALM.ENABLED, IMA_REALM.CREATED_AT)
            .from(IMA_REALM)
            .orderBy(IMA_REALM.CREATED_AT.desc())
            .offset(Math.max(0, offset))
            .limit(Math.max(0, limit))
            .fetch(this::mapRealm);
    }

    @Override
    public Realm create(Realm realm) {
        Record record = dsl.insertInto(IMA_REALM)
            .set(IMA_REALM.NAME, realm.getName())
            .set(IMA_REALM.ENABLED, realm.getEnabled() != null ? realm.getEnabled() : true)
            .returning(IMA_REALM.ID, IMA_REALM.NAME, IMA_REALM.ENABLED, IMA_REALM.CREATED_AT)
            .fetchOne();

        if (record == null) {
            throw new IllegalStateException("Failed to create realm");
        }

        return mapRealm(record);
    }

    @Override
    public Realm update(Realm realm) {
        Record record = dsl.update(IMA_REALM)
            .set(IMA_REALM.NAME, realm.getName())
            .set(IMA_REALM.ENABLED, realm.getEnabled())
            .where(IMA_REALM.ID.eq(realm.getId()))
            .returning(IMA_REALM.ID, IMA_REALM.NAME, IMA_REALM.ENABLED, IMA_REALM.CREATED_AT)
            .fetchOne();

        if (record == null) {
            throw new ResourceNotFoundException("Realm", realm.getId());
        }

        return mapRealm(record);
    }

    @Override
    public Realm enable(UUID id) {
        Record record = dsl.update(IMA_REALM)
            .set(IMA_REALM.ENABLED, true)
            .where(IMA_REALM.ID.eq(id))
            .returning(IMA_REALM.ID, IMA_REALM.NAME, IMA_REALM.ENABLED, IMA_REALM.CREATED_AT)
            .fetchOne();

        if (record == null) {
            throw new ResourceNotFoundException("Realm", id);
        }

        return mapRealm(record);
    }

    @Override
    public Realm disable(UUID id) {
        Record record = dsl.update(IMA_REALM)
            .set(IMA_REALM.ENABLED, false)
            .where(IMA_REALM.ID.eq(id))
            .returning(IMA_REALM.ID, IMA_REALM.NAME, IMA_REALM.ENABLED, IMA_REALM.CREATED_AT)
            .fetchOne();

        if (record == null) {
            throw new ResourceNotFoundException("Realm", id);
        }

        return mapRealm(record);
    }

    @Override
    public void delete(UUID id) {
        int deleted = dsl.deleteFrom(IMA_REALM)
            .where(IMA_REALM.ID.eq(id))
            .execute();
        if (deleted == 0) {
            throw new ResourceNotFoundException("Realm", id);
        }
    }

    @Override
    public long count() {
        Long count = dsl.selectCount()
            .from(IMA_REALM)
            .fetchOne(0, Long.class);
        return count != null ? count : 0L;
    }

    private Realm mapRealm(Record record) {
        Realm realm = new Realm();
        realm.setId(record.get(IMA_REALM.ID));
        realm.setName(record.get(IMA_REALM.NAME));
        realm.setEnabled(record.get(IMA_REALM.ENABLED));
        OffsetDateTime createdAt = record.get(IMA_REALM.CREATED_AT);
        realm.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        return realm;
    }
}
