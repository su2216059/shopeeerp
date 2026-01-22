package com.example.shopeeerp.pojo;

import java.time.LocalDateTime;

/**
 * 店铺API凭证实体
 * 注意: apiKey, apiSecret, accessToken, refreshToken 在数据库中加密存储
 */
public class ShopCredential {
    private Long id;
    private Long shopId;
    
    // API凭证
    private String clientId;
    private String apiKeyEncrypted;
    private String apiSecretEncrypted;
    
    // OAuth Token
    private String accessTokenEncrypted;
    private String refreshTokenEncrypted;
    private LocalDateTime tokenExpiresAt;
    
    // 状态
    private String credentialType;      // api_key/oauth/session
    private String status;              // active/expired/revoked
    private LocalDateTime lastUsedAt;
    private LocalDateTime lastVerifiedAt;
    
    // 限流
    private Integer rateLimitPerMinute;
    private Integer rateLimitPerDay;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ===== 解密后的字段 (不存储到数据库) =====
    private transient String apiKey;
    private transient String apiSecret;
    private transient String accessToken;
    private transient String refreshToken;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getApiKeyEncrypted() {
        return apiKeyEncrypted;
    }

    public void setApiKeyEncrypted(String apiKeyEncrypted) {
        this.apiKeyEncrypted = apiKeyEncrypted;
    }

    public String getApiSecretEncrypted() {
        return apiSecretEncrypted;
    }

    public void setApiSecretEncrypted(String apiSecretEncrypted) {
        this.apiSecretEncrypted = apiSecretEncrypted;
    }

    public String getAccessTokenEncrypted() {
        return accessTokenEncrypted;
    }

    public void setAccessTokenEncrypted(String accessTokenEncrypted) {
        this.accessTokenEncrypted = accessTokenEncrypted;
    }

    public String getRefreshTokenEncrypted() {
        return refreshTokenEncrypted;
    }

    public void setRefreshTokenEncrypted(String refreshTokenEncrypted) {
        this.refreshTokenEncrypted = refreshTokenEncrypted;
    }

    public LocalDateTime getTokenExpiresAt() {
        return tokenExpiresAt;
    }

    public void setTokenExpiresAt(LocalDateTime tokenExpiresAt) {
        this.tokenExpiresAt = tokenExpiresAt;
    }

    public String getCredentialType() {
        return credentialType;
    }

    public void setCredentialType(String credentialType) {
        this.credentialType = credentialType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public LocalDateTime getLastVerifiedAt() {
        return lastVerifiedAt;
    }

    public void setLastVerifiedAt(LocalDateTime lastVerifiedAt) {
        this.lastVerifiedAt = lastVerifiedAt;
    }

    public Integer getRateLimitPerMinute() {
        return rateLimitPerMinute;
    }

    public void setRateLimitPerMinute(Integer rateLimitPerMinute) {
        this.rateLimitPerMinute = rateLimitPerMinute;
    }

    public Integer getRateLimitPerDay() {
        return rateLimitPerDay;
    }

    public void setRateLimitPerDay(Integer rateLimitPerDay) {
        this.rateLimitPerDay = rateLimitPerDay;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // 解密字段的 getter/setter
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
