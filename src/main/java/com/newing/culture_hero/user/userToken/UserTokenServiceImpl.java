package com.newing.culture_hero.user.userToken;

import com.newing.culture_hero.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserTokenServiceImpl implements UserTokenService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final UserTokenRepository userTokenRepository;

    @Autowired
    public UserTokenServiceImpl(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }

    @Override
    public UserToken createAccessToken(User user) {
        // 24h
        return createToken(user, TokenType.ACCESS, 24);
    }

    @Override
    public UserToken createRefreshToken(User user) {
        // 30 days
        return createToken(user, TokenType.REFRESH, 24L * 30);
    }

    @Override
    public UserToken createToken(User user, TokenType type, long hoursValid) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusHours(hoursValid);

        UserToken token =
                new UserToken(
                        user,
                        generateTokenValue(type), // giá trị token
                        type,
                        now,
                        expiresAt);
        return userTokenRepository.save(token);
    }

    @Override
    public Optional<UserToken> findByToken(String token) {
        return userTokenRepository.findByToken(token);
    }

    @Override
    public boolean isValid(String token) {
        return findByToken(token)
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    @Override
    public boolean isValid(String token, TokenType expectedType) {
        return findByToken(token)
                .filter(t -> t.getTokenType() == expectedType)
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    @Transactional
    @Override
    public void deleteToken(String token) {
        userTokenRepository.deleteByToken(token);
    }

    @Override
    public void deleteTokensByUser(UUID userId) {
        userTokenRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteTokensByUserAndType(UUID userId, TokenType type) {
        userTokenRepository.deleteByUserIdAndTokenType(userId, type);
    }

    private String generateTokenValue(TokenType type) {
        // token ngẫu nhiên an toàn, gắn prefix để dễ nhận diện
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        String random = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return (type == TokenType.ACCESS ? "acc_" : "ref_") + random;
    }
}
