package com.example.shopeeerp.controller;

import com.example.shopeeerp.mapper.ShopMapper;
import com.example.shopeeerp.pojo.Shop;
import com.example.shopeeerp.pojo.ShopAccount;
import com.example.shopeeerp.pojo.ShopCredential;
import com.example.shopeeerp.security.ShopPermission;
import com.example.shopeeerp.security.SecurityUtil;
import com.example.shopeeerp.service.ShopService;
import com.example.shopeeerp.service.UserShopService;
import com.example.shopeeerp.util.CryptoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 店铺管理控制器
 */
@RestController
@RequestMapping("/api/shops")
@CrossOrigin
@Slf4j
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private UserShopService userShopService;

    @Autowired
    private ShopMapper shopMapper;

    // ========== 店铺管理 ==========

    /**
     * 获取所有店铺列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('SHOP_VIEW')")
    public ResponseEntity<List<Shop>> getAllShops() {
        log.info("获取店铺列表");
        try {
            return ResponseEntity.ok(shopService.getAllShops());
        } catch (IllegalArgumentException e) {
            log.warn("获取店铺列表失败: 原因={}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取店铺列表异常", e);
            throw e;
        }
    }

    /**
     * 获取店铺详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SHOP_VIEW')")
    @ShopPermission(param = "id")
    public ResponseEntity<Shop> getShop(@PathVariable Long id) {
        log.info("获取店铺详情: shopId={}", id);
        try {
            Shop shop = shopService.getShopById(id);
            if (shop == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(shop);
        } catch (IllegalArgumentException e) {
            log.warn("获取店铺详情失败: shopId={}, 原因={}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取店铺详情异常: shopId={}", id, e);
            throw e;
        }
    }

    /**
     * 创建店铺
     */
    @PostMapping
    @PreAuthorize("hasAuthority('SHOP_CREATE')")
    public ResponseEntity<Shop> createShop(@RequestBody Shop shop) {
        log.info("创建店铺: shopCode={}, platform={}, ownerUserId={}",
                shop != null ? shop.getShopCode() : null,
                shop != null ? shop.getPlatform() : null,
                shop != null ? shop.getOwnerUserId() : null);
        try {
            if (shop.getOwnerUserId() == null) {
                shop.setOwnerUserId(SecurityUtil.getCurrentUserId());
            }
            Shop created = shopService.createShop(shop);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            log.warn("创建店铺失败: shopCode={}, 原因={}", shop != null ? shop.getShopCode() : null, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("创建店铺异常: shopCode={}", shop != null ? shop.getShopCode() : null, e);
            throw e;
        }
    }

    /**
     * 更新店铺
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SHOP_UPDATE')")
    @ShopPermission(param = "id")
    public ResponseEntity<Shop> updateShop(@PathVariable Long id, @RequestBody Shop shop) {
        log.info("更新店铺: shopId={}", id);
        try {
            shop.setId(id);
            Shop updated = shopService.updateShop(shop);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.warn("更新店铺失败: shopId={}, 原因={}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("更新店铺异常: shopId={}", id, e);
            throw e;
        }
    }

    /**
     * 删除店铺
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SHOP_DELETE')")
    @ShopPermission(param = "id")
    public ResponseEntity<Void> deleteShop(@PathVariable Long id) {
        log.info("删除店铺: shopId={}", id);
        try {
            shopService.deleteShop(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("删除店铺失败: shopId={}, 原因={}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("删除店铺异常: shopId={}", id, e);
            throw e;
        }
    }

    /**
     * 获取默认店铺
     */
    @GetMapping("/default")
    @PreAuthorize("hasAuthority('SHOP_VIEW')")
    public ResponseEntity<Shop> getDefaultShop() {
        log.info("获取默认店铺");
        try {
            Shop shop = shopService.getDefaultShop();
            if (shop == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(shop);
        } catch (IllegalArgumentException e) {
            log.warn("获取默认店铺失败: 原因={}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取默认店铺异常", e);
            throw e;
        }
    }

    /**
     * 按平台获取店铺
     */
    @GetMapping("/platform/{platform}")
    @PreAuthorize("hasAuthority('SHOP_VIEW')")
    public ResponseEntity<List<Shop>> getShopsByPlatform(@PathVariable String platform) {
        log.info("按平台获取店铺: platform={}", platform);
        try {
            return ResponseEntity.ok(shopService.getShopsByPlatform(platform));
        } catch (IllegalArgumentException e) {
            log.warn("按平台获取店铺失败: platform={}, 原因={}", platform, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("按平台获取店铺异常: platform={}", platform, e);
            throw e;
        }
    }

    // ========== API凭证管理 ==========

    /**
     * 获取店铺的API凭证 (掩码显示敏感信息)
     */
    @GetMapping("/{shopId}/credential")
    @PreAuthorize("hasAuthority('SHOP_CREDENTIAL')")
    @ShopPermission
    public ResponseEntity<Map<String, Object>> getCredential(@PathVariable Long shopId) {
        log.info("??????: shopId={}", shopId);
        try {
            ShopCredential credential = shopService.getCredential(shopId);
            if (credential == null) {
                return ResponseEntity.notFound().build();
            }

            // ??????
            Map<String, Object> result = new HashMap<>();
            result.put("shopId", credential.getShopId());
            result.put("clientId", credential.getClientId());
            result.put("apiKey", CryptoUtil.mask(credential.getApiKey()));
            result.put("credentialType", credential.getCredentialType());
            result.put("status", credential.getStatus());
            result.put("lastUsedAt", credential.getLastUsedAt());
            result.put("lastVerifiedAt", credential.getLastVerifiedAt());
            result.put("rateLimitPerMinute", credential.getRateLimitPerMinute());
            result.put("rateLimitPerDay", credential.getRateLimitPerDay());

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("????????: shopId={}, ??={}", shopId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("????????: shopId={}", shopId, e);
            throw e;
        }
    }

    /**
     * 保存/更新API凭证
     */
    @PostMapping("/{shopId}/credential")
    @PreAuthorize("hasAuthority('SHOP_CREDENTIAL')")
    @ShopPermission
    public ResponseEntity<Map<String, Object>> saveCredential(
            @PathVariable Long shopId,
            @RequestBody CredentialRequest request) {

        log.info("??????: shopId={}, hasClientId={}", shopId, request != null && request.getClientId() != null);
        try {
            ShopCredential credential = shopService.saveCredential(
                    shopId,
                    request.getClientId(),
                    request.getApiKey(),
                    request.getApiSecret()
            );

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("shopId", shopId);
            result.put("clientId", credential.getClientId());
            result.put("message", "Credential saved successfully");

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("????????: shopId={}, ??={}", shopId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("????????: shopId={}", shopId, e);
            throw e;
        }
    }

    /**
     * 验证凭证
     */
    @PostMapping("/{shopId}/credential/verify")
    @PreAuthorize("hasAuthority('SHOP_CREDENTIAL')")
    @ShopPermission
    public ResponseEntity<Map<String, Object>> verifyCredential(@PathVariable Long shopId) {
        log.info("??????: shopId={}", shopId);
        try {
            boolean valid = shopService.verifyCredential(shopId);

            Map<String, Object> result = new HashMap<>();
            result.put("shopId", shopId);
            result.put("valid", valid);
            result.put("message", valid ? "Credential is valid" : "Credential verification failed");

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("????????: shopId={}, ??={}", shopId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("????????: shopId={}", shopId, e);
            throw e;
        }
    }

    // ========== 登录账号管理 ==========

    /**
     * 获取店铺的登录账号列表
     */
    @GetMapping("/{shopId}/accounts")
    @PreAuthorize("hasAuthority('SHOP_ACCOUNT')")
    @ShopPermission
    public ResponseEntity<List<Map<String, Object>>> getAccounts(@PathVariable Long shopId) {
        log.info("????????: shopId={}", shopId);
        try {
            List<ShopAccount> accounts = shopService.getAccountsByShopId(shopId);

            // ?????
            List<Map<String, Object>> result = accounts.stream().map(acc -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", acc.getId());
                map.put("shopId", acc.getShopId());
                map.put("accountType", acc.getAccountType());
                map.put("accountName", acc.getAccountName());
                map.put("username", acc.getUsername());
                map.put("status", acc.getStatus());
                map.put("lastLoginAt", acc.getLastLoginAt());
                map.put("remark", acc.getRemark());
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("??????????: shopId={}, ??={}", shopId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("??????????: shopId={}", shopId, e);
            throw e;
        }
    }

    /**
     * 添加登录账号
     */
    @PostMapping("/{shopId}/accounts")
    @PreAuthorize("hasAuthority('SHOP_ACCOUNT')")
    @ShopPermission
    public ResponseEntity<ShopAccount> addAccount(
            @PathVariable Long shopId,
            @RequestBody ShopAccount account) {

        log.info("??????: shopId={}, accountType={}, username={}",
                shopId,
                account != null ? account.getAccountType() : null,
                account != null ? account.getUsername() : null);
        try {
            ShopAccount created = shopService.addAccount(shopId, account);
            // ???????
            created.setPassword(null);
            created.setPasswordEncrypted(null);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            log.warn("????????: shopId={}, ??={}", shopId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("????????: shopId={}", shopId, e);
            throw e;
        }
    }

    /**
     * 更新登录账号
     */
    @PutMapping("/{shopId}/accounts/{accountId}")
    @PreAuthorize("hasAuthority('SHOP_ACCOUNT')")
    @ShopPermission
    public ResponseEntity<ShopAccount> updateAccount(
            @PathVariable Long shopId,
            @PathVariable Long accountId,
            @RequestBody ShopAccount account) {

        log.info("??????: shopId={}, accountId={}", shopId, accountId);
        try {
            account.setId(accountId);
            account.setShopId(shopId);
            ShopAccount updated = shopService.updateAccount(account);
            // ???????
            updated.setPassword(null);
            updated.setPasswordEncrypted(null);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.warn("????????: shopId={}, accountId={}, ??={}", shopId, accountId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("????????: shopId={}, accountId={}", shopId, accountId, e);
            throw e;
        }
    }

    /**
     * 删除登录账号
     */
    @DeleteMapping("/{shopId}/accounts/{accountId}")
    @PreAuthorize("hasAuthority('SHOP_ACCOUNT')")
    @ShopPermission
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Long shopId,
            @PathVariable Long accountId) {

        log.info("??????: shopId={}, accountId={}", shopId, accountId);
        try {
            shopService.deleteAccount(accountId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("????????: shopId={}, accountId={}, ??={}", shopId, accountId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("????????: shopId={}, accountId={}", shopId, accountId, e);
            throw e;
        }
    }

    /**
     * 获取账号详情（包含解密的密码，需要权限控制）
     */
    @GetMapping("/{shopId}/accounts/{accountId}/detail")
    @PreAuthorize("hasAuthority('SHOP_ACCOUNT')")
    @ShopPermission
    public ResponseEntity<ShopAccount> getAccountDetail(
            @PathVariable Long shopId,
            @PathVariable Long accountId) {

        log.info("????????: shopId={}, accountId={}", shopId, accountId);
        try {
            ShopAccount account = shopService.getAccountById(accountId);
            if (account == null || !account.getShopId().equals(shopId)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(account);
        } catch (IllegalArgumentException e) {
            log.warn("??????????: shopId={}, accountId={}, ??={}", shopId, accountId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("??????????: shopId={}, accountId={}", shopId, accountId, e);
            throw e;
        }
    }

    // ========== 店铺切换 ==========

    /**
     * 切换当前店铺
     */
    @PostMapping("/{shopId}/switch")
    @PreAuthorize("hasAuthority('SHOP_VIEW')")
    @ShopPermission
    public ResponseEntity<Map<String, Object>> switchShop(@PathVariable Long shopId) {
        log.info("????: shopId={}", shopId);
        try {
            shopService.switchShop(shopId);
            Shop shop = shopService.getCurrentShop();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("currentShop", shop);
            result.put("message", "Switched to shop: " + shop.getShopName());

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("??????: shopId={}, ??={}", shopId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("??????: shopId={}", shopId, e);
            throw e;
        }
    }

    /**
     * 获取当前店铺
     */
    @GetMapping("/current")
    @PreAuthorize("hasAuthority('SHOP_VIEW')")
    public ResponseEntity<Shop> getCurrentShop() {
        log.info("??????");
        try {
            Shop shop = shopService.getCurrentShop();
            if (shop == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(shop);
        } catch (IllegalArgumentException e) {
            log.warn("????????: ??={}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("????????", e);
            throw e;
        }
    }

    /**
     * Bind shop with credentials.
     */
    @PostMapping("/bind")
    @PreAuthorize("hasAuthority('SHOP_VIEW')")
    public ResponseEntity<Map<String, Object>> bindShop(@RequestBody BindShopRequest request) {
        log.info("????: shopId={}, shopCode={}, platform={}, market={}, hasClientId={}",
                request != null ? request.getShopId() : null,
                request != null ? request.getShopCode() : null,
                request != null ? request.getPlatform() : null,
                request != null ? request.getMarket() : null,
                request != null && request.getClientId() != null);
        Map<String, Object> result = new HashMap<>();

        try {
            Long userId = SecurityUtil.getCurrentUserId();
            if (userId == null) {
                result.put("success", false);
                result.put("message", "Unauthorized");
                return ResponseEntity.status(401).body(result);
            }

            if (request.getClientId() == null || request.getClientId().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "clientId is required");
                return ResponseEntity.badRequest().body(result);
            }
            if (request.getApiKey() == null || request.getApiKey().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "apiKey is required");
                return ResponseEntity.badRequest().body(result);
            }

            Shop shop = null;
            if (request.getShopId() != null) {
                shop = shopMapper.selectById(request.getShopId());
            } else if (request.getShopCode() != null && !request.getShopCode().trim().isEmpty()) {
                shop = shopMapper.selectByCode(request.getShopCode().trim());
            }

            if (shop == null) {
                if (request.getShopCode() == null || request.getShopCode().trim().isEmpty()) {
                    result.put("success", false);
                    result.put("message", "shopCode is required");
                    return ResponseEntity.badRequest().body(result);
                }
                Shop create = new Shop();
                create.setShopCode(request.getShopCode().trim());
                create.setShopName(request.getShopName() == null || request.getShopName().trim().isEmpty()
                        ? request.getShopCode().trim()
                        : request.getShopName().trim());
                create.setPlatform(request.getPlatform() == null ? "ozon" : request.getPlatform());
                create.setMarket(request.getMarket() == null ? "RU" : request.getMarket());
                create.setOwnerUserId(userId);
                shop = shopService.createShop(create);
            } else if (!SecurityUtil.isAdmin() && !userShopService.hasShopAccess(userId, shop.getId())) {
                userShopService.addUserShop(userId, shop.getId(), "owner");
            }

            shopService.saveCredential(shop.getId(),
                    request.getClientId().trim(),
                    request.getApiKey().trim(),
                    request.getApiSecret());

            result.put("success", true);
            result.put("shopId", shop.getId());
            result.put("shopCode", shop.getShopCode());
            result.put("message", "Shop bound");

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("??????: shopCode={}, ??={}", request != null ? request.getShopCode() : null, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("??????: shopCode={}", request != null ? request.getShopCode() : null, e);
            throw e;
        }
    }
    // ========== Request Classes ==========

    public static class CredentialRequest {
        private String clientId;
        private String apiKey;
        private String apiSecret;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

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
    }

    public static class BindShopRequest {
        private Long shopId;
        private String shopCode;
        private String shopName;
        private String platform;
        private String market;
        private String clientId;
        private String apiKey;
        private String apiSecret;

        public Long getShopId() {
            return shopId;
        }

        public void setShopId(Long shopId) {
            this.shopId = shopId;
        }

        public String getShopCode() {
            return shopCode;
        }

        public void setShopCode(String shopCode) {
            this.shopCode = shopCode;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getMarket() {
            return market;
        }

        public void setMarket(String market) {
            this.market = market;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

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
    }

}
