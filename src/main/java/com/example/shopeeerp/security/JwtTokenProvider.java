package com.example.shopeeerp.security;

import com.example.shopeeerp.pojo.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {
    private static final String CLAIM_USER_ID = "user_id";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_ROLE_ID = "role_id";
    private static final String CLAIM_PERMISSIONS = "permissions";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final Key signingKey;
    private final long accessTokenMillis;
    private final long refreshTokenMillis;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-minutes:120}") long accessTokenMinutes,
            @Value("${app.jwt.refresh-token-hours:168}") long refreshTokenHours) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenMillis = accessTokenMinutes * 60 * 1000;
        this.refreshTokenMillis = refreshTokenHours * 60 * 60 * 1000;
    }

    public String generateAccessToken(User user, java.util.List<String> permissions) {
        Map<String, Object> claims = baseClaims(user, permissions);
        claims.put(CLAIM_TYPE, TYPE_ACCESS);
        return buildToken(claims, user.getUsername(), accessTokenMillis);
    }

    public String generateRefreshToken(User user, java.util.List<String> permissions) {
        Map<String, Object> claims = baseClaims(user, permissions);
        claims.put(CLAIM_TYPE, TYPE_REFRESH);
        return buildToken(claims, user.getUsername(), refreshTokenMillis);
    }

    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        return TYPE_ACCESS.equals(getTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return TYPE_REFRESH.equals(getTokenType(token));
    }

    public JwtUserPrincipal getPrincipal(String token) {
        Claims claims = parseClaims(token);
        Long userId = getLongClaim(claims, CLAIM_USER_ID);
        String username = claims.get(CLAIM_USERNAME, String.class);
        Long roleId = getLongClaim(claims, CLAIM_ROLE_ID);
        java.util.List<String> permissions = extractPermissions(claims);
        return new JwtUserPrincipal(userId, username, roleId, permissions);
    }

    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return getLongClaim(claims, CLAIM_USER_ID);
    }

    public long getAccessTokenExpiresInSeconds() {
        return accessTokenMillis / 1000;
    }

    public long getRefreshTokenExpiresInSeconds() {
        return refreshTokenMillis / 1000;
    }

    private Map<String, Object> baseClaims(User user, java.util.List<String> permissions) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USER_ID, user.getUserId());
        claims.put(CLAIM_USERNAME, user.getUsername());
        claims.put(CLAIM_ROLE_ID, user.getRoleId());
        if (permissions != null && !permissions.isEmpty()) {
            claims.put(CLAIM_PERMISSIONS, permissions);
        }
        return claims;
    }

    private String buildToken(Map<String, Object> claims, String subject, long expiresInMillis) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiresInMillis);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String getTokenType(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.get(CLAIM_TYPE, String.class);
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    private Long getLongClaim(Claims claims, String key) {
        Object value = claims.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private java.util.List<String> extractPermissions(Claims claims) {
        Object value = claims.get(CLAIM_PERMISSIONS);
        if (value instanceof java.util.List) {
            java.util.List<?> list = (java.util.List<?>) value;
            java.util.List<String> result = new java.util.ArrayList<>();
            for (Object item : list) {
                if (item != null) {
                    result.add(String.valueOf(item));
                }
            }
            return result;
        }
        return java.util.Collections.emptyList();
    }
}
