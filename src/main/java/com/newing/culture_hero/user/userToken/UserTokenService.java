package com.newing.culture_hero.user.userToken;

import com.newing.culture_hero.user.User;
import java.util.Optional;
import java.util.UUID;

public interface UserTokenService {
    UserToken createAccessToken(User user);

    UserToken createRefreshToken(User user);

    UserToken createToken(User user, TokenType type, long hoursValid);

    Optional<UserToken> findByToken(String token);

    boolean isValid(String token); // kiểm tra còn hạn

    boolean isValid(String token, TokenType expectedType);

    void deleteToken(String token);

    void deleteTokensByUser(UUID userId); // xoá mọi token của user

    void deleteTokensByUserAndType(UUID userId, TokenType type);
}
