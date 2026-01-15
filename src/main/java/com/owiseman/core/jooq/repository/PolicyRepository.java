package com.owiseman.core.jooq.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.owiseman.core.domain.Policy;
import com.owiseman.core.domain.PolicyType;

public interface PolicyRepository {

    Optional<Policy> findById(UUID id);

    List<Policy> findByType(UUID realmId, PolicyType type);

    List<Policy> findAll(UUID realmId, int offset, int limit);

    Policy create(Policy policy);

    Policy update(Policy policy);

    void delete(UUID id);

    long countByRealm(UUID realmId);
}
