package com.owiseman.core.jooq.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.owiseman.core.domain.Client;

public interface ClientRepository {

    Optional<Client> findById(UUID id);

    Optional<Client> findByClientId(UUID realmId, String clientId);

    Optional<Client> authenticate(UUID realmId, String clientId, String clientSecret);

    List<Client> findAll(UUID realmId, int offset, int limit);

    Client create(Client client);

    Client update(Client client);

    void delete(UUID id);

    long countByRealm(UUID realmId);
} 
