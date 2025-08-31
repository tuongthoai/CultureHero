package com.newing.culture_hero.user.userToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, UUID> {
    Optional<UserToken> findByToken(String token);

    void deleteByToken(String token);

    void deleteByUserId(UUID userId);

    void deleteByUserIdAndTokenType(UUID userId, TokenType tokenType);
}
