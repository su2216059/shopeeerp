package com.example.shopeeerp.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TokenSessionService {
    private static final String REFRESH_KEY_PREFIX = "auth:refresh:user:";
    private static final String ACCESS_BLACKLIST_PREFIX = "auth:blacklist:access:";

    private final StringRedisTemplate stringRedisTemplate;
    private final boolean redisEnabled;

    private final Map<Long, LocalTokenRecord> localRefreshStore = new ConcurrentHashMap<>();
    private final Map<String, Long> localAccessBlacklist = new ConcurrentHashMap<>();

    public TokenSessionService(
            @Autowired(required = false) StringRedisTemplate stringRedisTemplate,
            @Value("${app.redis.enabled:true}") boolean redisEnabled) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisEnabled = redisEnabled;
    }

    public void saveRefreshToken(Long userId, String refreshToken, long ttlSeconds) {
        if (userId == null || refreshToken == null || refreshToken.trim().isEmpty()) {
            return;
        }
        long safeTtl = Math.max(1, ttlSeconds);
        String token = refreshToken.trim();

        if (isRedisAvailable()) {
            try {
                stringRedisTemplate.opsForValue().set(refreshKey(userId), token, Duration.ofSeconds(safeTtl));
                return;
            } catch (DataAccessException ex) {
                log.warn("save refresh token to redis failed, fallback local: userId={}", userId);
            }
        }
        localRefreshStore.put(userId, new LocalTokenRecord(token, System.currentTimeMillis() + safeTtl * 1000));
    }

    public boolean isRefreshTokenValid(Long userId, String refreshToken) {
        if (userId == null || refreshToken == null || refreshToken.trim().isEmpty()) {
            return false;
        }
        String token = refreshToken.trim();

        if (isRedisAvailable()) {
            try {
                String stored = stringRedisTemplate.opsForValue().get(refreshKey(userId));
                return token.equals(stored);
            } catch (DataAccessException ex) {
                log.warn("validate refresh token from redis failed, fallback local: userId={}", userId);
            }
        }

        LocalTokenRecord record = localRefreshStore.get(userId);
        if (record == null) {
            return false;
        }
        if (record.expiresAtMillis <= System.currentTimeMillis()) {
            localRefreshStore.remove(userId);
            return false;
        }
        return token.equals(record.token);
    }

    public void revokeRefreshToken(Long userId) {
        if (userId == null) {
            return;
        }
        if (isRedisAvailable()) {
            try {
                stringRedisTemplate.delete(refreshKey(userId));
            } catch (DataAccessException ex) {
                log.warn("revoke refresh token in redis failed, fallback local: userId={}", userId);
            }
        }
        localRefreshStore.remove(userId);
    }

    public void blacklistAccessToken(String accessToken, long ttlSeconds) {
        if (accessToken == null || accessToken.trim().isEmpty() || ttlSeconds <= 0) {
            return;
        }
        String tokenHash = tokenHash(accessToken.trim());
        long safeTtl = Math.max(1, ttlSeconds);

        if (isRedisAvailable()) {
            try {
                stringRedisTemplate.opsForValue().set(accessBlacklistKey(tokenHash), "1", Duration.ofSeconds(safeTtl));
                return;
            } catch (DataAccessException ex) {
                log.warn("blacklist access token in redis failed, fallback local");
            }
        }
        localAccessBlacklist.put(tokenHash, System.currentTimeMillis() + safeTtl * 1000);
    }

    public boolean isAccessTokenBlacklisted(String accessToken) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            return false;
        }
        String tokenHash = tokenHash(accessToken.trim());
        if (isRedisAvailable()) {
            try {
                Boolean exists = stringRedisTemplate.hasKey(accessBlacklistKey(tokenHash));
                return Boolean.TRUE.equals(exists);
            } catch (DataAccessException ex) {
                log.warn("check access token blacklist from redis failed, fallback local");
            }
        }
        Long expiresAt = localAccessBlacklist.get(tokenHash);
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt <= System.currentTimeMillis()) {
            localAccessBlacklist.remove(tokenHash);
            return false;
        }
        return true;
    }

    private boolean isRedisAvailable() {
        return redisEnabled && stringRedisTemplate != null;
    }

    private String refreshKey(Long userId) {
        return REFRESH_KEY_PREFIX + userId;
    }

    private String accessBlacklistKey(String tokenHash) {
        return ACCESS_BLACKLIST_PREFIX + tokenHash;
    }

    private String tokenHash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            return Integer.toHexString(token.hashCode());
        }
    }

    private static class LocalTokenRecord {
        private final String token;
        private final long expiresAtMillis;

        private LocalTokenRecord(String token, long expiresAtMillis) {
            this.token = token;
            this.expiresAtMillis = expiresAtMillis;
        }
    }
}

