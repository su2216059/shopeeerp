package com.example.shopeeerp.controller;

import com.example.shopeeerp.pojo.Shop;
import com.example.shopeeerp.pojo.ShopAccount;
import com.example.shopeeerp.pojo.ShopCredential;
import com.example.shopeeerp.service.ShopService;
import com.example.shopeeerp.util.CryptoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
public class ShopController {

    @Autowired
    private ShopService shopService;

    // ========== 店铺管理 ==========

    /**
     * 获取所有店铺列表
     */
    @GetMapping
    public ResponseEntity<List<Shop>> getAllShops() {
        return ResponseEntity.ok(shopService.getAllShops());
    }

    /**
     * 获取店铺详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Shop> getShop(@PathVariable Long id) {
        Shop shop = shopService.getShopById(id);
        if (shop == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shop);
    }

    /**
     * 创建店铺
     */
    @PostMapping
    public ResponseEntity<Shop> createShop(@RequestBody Shop shop) {
        Shop created = shopService.createShop(shop);
        return ResponseEntity.ok(created);
    }

    /**
     * 更新店铺
     */
    @PutMapping("/{id}")
    public ResponseEntity<Shop> updateShop(@PathVariable Long id, @RequestBody Shop shop) {
        shop.setId(id);
        Shop updated = shopService.updateShop(shop);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除店铺
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShop(@PathVariable Long id) {
        shopService.deleteShop(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取默认店铺
     */
    @GetMapping("/default")
    public ResponseEntity<Shop> getDefaultShop() {
        Shop shop = shopService.getDefaultShop();
        if (shop == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shop);
    }

    /**
     * 按平台获取店铺
     */
    @GetMapping("/platform/{platform}")
    public ResponseEntity<List<Shop>> getShopsByPlatform(@PathVariable String platform) {
        return ResponseEntity.ok(shopService.getShopsByPlatform(platform));
    }

    // ========== API凭证管理 ==========

    /**
     * 获取店铺的API凭证 (掩码显示敏感信息)
     */
    @GetMapping("/{shopId}/credential")
    public ResponseEntity<Map<String, Object>> getCredential(@PathVariable Long shopId) {
        ShopCredential credential = shopService.getCredential(shopId);
        if (credential == null) {
            return ResponseEntity.notFound().build();
        }

        // 掩码敏感信息
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
    }

    /**
     * 保存/更新API凭证
     */
    @PostMapping("/{shopId}/credential")
    public ResponseEntity<Map<String, Object>> saveCredential(
            @PathVariable Long shopId,
            @RequestBody CredentialRequest request) {
        
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
    }

    /**
     * 验证凭证
     */
    @PostMapping("/{shopId}/credential/verify")
    public ResponseEntity<Map<String, Object>> verifyCredential(@PathVariable Long shopId) {
        boolean valid = shopService.verifyCredential(shopId);

        Map<String, Object> result = new HashMap<>();
        result.put("shopId", shopId);
        result.put("valid", valid);
        result.put("message", valid ? "Credential is valid" : "Credential verification failed");

        return ResponseEntity.ok(result);
    }

    // ========== 登录账号管理 ==========

    /**
     * 获取店铺的登录账号列表
     */
    @GetMapping("/{shopId}/accounts")
    public ResponseEntity<List<Map<String, Object>>> getAccounts(@PathVariable Long shopId) {
        List<ShopAccount> accounts = shopService.getAccountsByShopId(shopId);
        
        // 不返回密码
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
    }

    /**
     * 添加登录账号
     */
    @PostMapping("/{shopId}/accounts")
    public ResponseEntity<ShopAccount> addAccount(
            @PathVariable Long shopId,
            @RequestBody ShopAccount account) {
        
        ShopAccount created = shopService.addAccount(shopId, account);
        // 清除密码后返回
        created.setPassword(null);
        created.setPasswordEncrypted(null);
        return ResponseEntity.ok(created);
    }

    /**
     * 更新登录账号
     */
    @PutMapping("/{shopId}/accounts/{accountId}")
    public ResponseEntity<ShopAccount> updateAccount(
            @PathVariable Long shopId,
            @PathVariable Long accountId,
            @RequestBody ShopAccount account) {
        
        account.setId(accountId);
        account.setShopId(shopId);
        ShopAccount updated = shopService.updateAccount(account);
        // 清除密码后返回
        updated.setPassword(null);
        updated.setPasswordEncrypted(null);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除登录账号
     */
    @DeleteMapping("/{shopId}/accounts/{accountId}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Long shopId,
            @PathVariable Long accountId) {
        
        shopService.deleteAccount(accountId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取账号详情（包含解密的密码，需要权限控制）
     */
    @GetMapping("/{shopId}/accounts/{accountId}/detail")
    public ResponseEntity<ShopAccount> getAccountDetail(
            @PathVariable Long shopId,
            @PathVariable Long accountId) {
        
        ShopAccount account = shopService.getAccountById(accountId);
        if (account == null || !account.getShopId().equals(shopId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }

    // ========== 店铺切换 ==========

    /**
     * 切换当前店铺
     */
    @PostMapping("/{shopId}/switch")
    public ResponseEntity<Map<String, Object>> switchShop(@PathVariable Long shopId) {
        shopService.switchShop(shopId);
        Shop shop = shopService.getCurrentShop();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("currentShop", shop);
        result.put("message", "Switched to shop: " + shop.getShopName());

        return ResponseEntity.ok(result);
    }

    /**
     * 获取当前店铺
     */
    @GetMapping("/current")
    public ResponseEntity<Shop> getCurrentShop() {
        Shop shop = shopService.getCurrentShop();
        if (shop == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shop);
    }

    // ========== 请求体类 ==========

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
}
