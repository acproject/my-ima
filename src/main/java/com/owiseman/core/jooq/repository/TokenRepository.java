package com.owiseman.core.jooq.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.owiseman.core.domain.Token;
import com.owiseman.core.domain.TokenType;

public interface TokenRepository {

    Optional<Token> findById(UUID id);

    List<Token> findByUserAndType(UUID userId, TokenType type);

    List<Token> findByClientAndType(UUID clientId, TokenType type, int offset, int limit);

    Token create(Token token);

    void revoke(UUID tokenId);

    void revokeAllForUser(UUID userId);

    int deleteExpired();

    long countByRealm(UUID realmId);
}
