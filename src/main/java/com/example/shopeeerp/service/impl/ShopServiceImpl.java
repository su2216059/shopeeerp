package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.ShopAccountMapper;
import com.example.shopeeerp.mapper.ShopCredentialMapper;
import com.example.shopeeerp.mapper.ShopMapper;
import com.example.shopeeerp.pojo.Shop;
import com.example.shopeeerp.pojo.ShopAccount;
import com.example.shopeeerp.pojo.ShopCredential;
import com.example.shopeeerp.security.SecurityUtil;
import com.example.shopeeerp.service.ShopService;
import com.example.shopeeerp.service.UserShopService;
import com.example.shopeeerp.util.CryptoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl implements ShopService {

    private static final Logger log = LoggerFactory.getLogger(ShopServiceImpl.class);

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private ShopCredentialMapper credentialMapper;

    @Autowired
    private ShopAccountMapper accountMapper;

    @Autowired
    private CryptoUtil cryptoUtil;

    @Autowired
    private UserShopService userShopService;

    private static final ThreadLocal<Shop> currentShopContext = new ThreadLocal<>();
    private static final ThreadLocal<ShopCredential> currentCredentialContext = new ThreadLocal<>();

    @Override
    @Transactional
    public Shop createShop(Shop shop) {
        if (shop.getStatus() == null) {
            shop.setStatus("active");
        }
        if (shop.getPlatform() == null) {
            shop.setPlatform("ozon");
        }
        if (shop.getMarket() == null) {
            shop.setMarket("RU");
        }
        if (shop.getTimezone() == null) {
            shop.setTimezone("Europe/Moscow");
        }
        if (shop.getCurrency() == null) {
            shop.setCurrency("RUB");
        }

        shopMapper.insert(shop);
        if (shop.getOwnerUserId() != null) {
            userShopService.addUserShop(shop.getOwnerUserId(), shop.getId(), "owner");
        }
        log.info("Created shop: {} ({})", shop.getShopCode(), shop.getId());
        return shop;
    }

    @Override
    @Transactional
    public Shop updateShop(Shop shop) {
        if (shop.getId() != null) {
            assertShopAccess(shop.getId());
        }
        shopMapper.update(shop);
        log.info("Updated shop: {}", shop.getShopCode());
        return shop;
    }

    @Override
    @Transactional
    public void deleteShop(Long shopId) {
        assertShopAccess(shopId);
        credentialMapper.deleteByShopId(shopId);
        accountMapper.deleteByShopId(shopId);
        shopMapper.deleteById(shopId);
        log.info("Deleted shop: {}", shopId);
    }

    @Override
    public Shop getShopById(Long shopId) {
        if (!canAccessShop(shopId)) {
            return null;
        }
        return shopMapper.selectById(shopId);
    }

    @Override
    public Shop getShopByCode(String shopCode) {
        Shop shop = shopMapper.selectByCode(shopCode);
        if (shop == null) {
            return null;
        }
        if (!canAccessShop(shop.getId())) {
            return null;
        }
        return shop;
    }

    @Override
    public Shop getDefaultShop() {
        Shop shop = shopMapper.selectDefault();
        if (shop == null) {
            return null;
        }
        if (!canAccessShop(shop.getId())) {
            return null;
        }
        return shop;
    }

    @Override
    public List<Shop> getShopsByUserId(Long userId) {
        return shopMapper.selectByUserId(userId);
    }

    @Override
    public List<Shop> getAllShops() {
        if (SecurityUtil.isAdmin()) {
            return shopMapper.selectAll();
        }
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Collections.emptyList();
        }
        return shopMapper.selectByUserId(userId);
    }

    @Override
    public List<Shop> getShopsByPlatform(String platform) {
        if (SecurityUtil.isAdmin()) {
            return shopMapper.selectByPlatform(platform);
        }
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Collections.emptyList();
        }
        List<Shop> shops = shopMapper.selectByUserId(userId);
        if (shops == null || shops.isEmpty()) {
            return Collections.emptyList();
        }
        return shops.stream()
                .filter(shop -> platform != null && platform.equalsIgnoreCase(shop.getPlatform()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShopCredential saveCredential(Long shopId, String clientId, String apiKey, String apiSecret) {
        assertShopAccess(shopId);
        ShopCredential credential = new ShopCredential();
        credential.setShopId(shopId);
        credential.setClientId(clientId);
        credential.setCredentialType("api_key");
        credential.setStatus("active");

        if (apiKey != null && !apiKey.isEmpty()) {
            credential.setApiKeyEncrypted(cryptoUtil.encrypt(apiKey));
        }
        if (apiSecret != null && !apiSecret.isEmpty()) {
            credential.setApiSecretEncrypted(cryptoUtil.encrypt(apiSecret));
        }

        credentialMapper.upsert(credential);
        log.info("Saved credential for shop: {}, clientId: {}", shopId, clientId);

        return getCredential(shopId);
    }

    @Override
    public ShopCredential getCredential(Long shopId) {
        assertShopAccess(shopId);
        ShopCredential credential = credentialMapper.selectByShopId(shopId);
        if (credential != null) {
            decryptCredential(credential);
        }
        return credential;
    }

    @Override
    public ShopCredential getCredentialByShopCode(String shopCode) {
        Shop shop = getShopByCode(shopCode);
        if (shop == null) {
            return null;
        }
        return getCredential(shop.getId());
    }

    @Override
    public boolean verifyCredential(Long shopId) {
        assertShopAccess(shopId);
        ShopCredential credential = getCredential(shopId);
        if (credential == null || credential.getApiKey() == null) {
            return false;
        }

        credentialMapper.updateLastVerified(shopId);
        return true;
    }

    @Override
    public void updateCredentialStatus(Long shopId, String status) {
        assertShopAccess(shopId);
        credentialMapper.updateStatus(shopId, status);
    }

    @Override
    public void markCredentialUsed(Long shopId) {
        assertShopAccess(shopId);
        credentialMapper.updateLastUsed(shopId);
    }

    @Override
    @Transactional
    public ShopAccount addAccount(Long shopId, ShopAccount account) {
        assertShopAccess(shopId);
        account.setShopId(shopId);
        if (account.getStatus() == null) {
            account.setStatus("active");
        }

        if (account.getPassword() != null && !account.getPassword().isEmpty()) {
            account.setPasswordEncrypted(cryptoUtil.encrypt(account.getPassword()));
        }

        accountMapper.insert(account);
        log.info("Added account for shop {}: {} ({})", shopId, account.getUsername(), account.getAccountType());
        return account;
    }

    @Override
    @Transactional
    public ShopAccount updateAccount(ShopAccount account) {
        if (account.getShopId() != null) {
            assertShopAccess(account.getShopId());
        } else if (account.getId() != null) {
            ShopAccount stored = accountMapper.selectById(account.getId());
            if (stored != null && stored.getShopId() != null) {
                assertShopAccess(stored.getShopId());
            }
        }

        if (account.getPassword() != null && !account.getPassword().isEmpty()) {
            account.setPasswordEncrypted(cryptoUtil.encrypt(account.getPassword()));
        }

        accountMapper.update(account);
        log.info("Updated account: {}", account.getId());
        return account;
    }

    @Override
    @Transactional
    public void deleteAccount(Long accountId) {
        ShopAccount account = accountMapper.selectById(accountId);
        if (account != null && account.getShopId() != null) {
            assertShopAccess(account.getShopId());
        }
        accountMapper.deleteById(accountId);
        log.info("Deleted account: {}", accountId);
    }

    @Override
    public List<ShopAccount> getAccountsByShopId(Long shopId) {
        assertShopAccess(shopId);
        return accountMapper.selectByShopId(shopId);
    }

    @Override
    public ShopAccount getAccountById(Long accountId) {
        ShopAccount account = accountMapper.selectById(accountId);
        if (account != null && account.getShopId() != null) {
            assertShopAccess(account.getShopId());
        }
        if (account != null && account.getPasswordEncrypted() != null) {
            try {
                account.setPassword(cryptoUtil.decrypt(account.getPasswordEncrypted()));
            } catch (Exception e) {
                log.error("Failed to decrypt password for account: {}", accountId, e);
            }
        }
        return account;
    }

    @Override
    public void switchShop(Long shopId) {
        assertShopAccess(shopId);
        Shop shop = getShopById(shopId);
        if (shop == null) {
            throw new IllegalArgumentException("Shop not found: " + shopId);
        }

        ShopCredential credential = getCredential(shopId);

        currentShopContext.set(shop);
        currentCredentialContext.set(credential);

        log.debug("Switched to shop: {} ({})", shop.getShopCode(), shop.getId());
    }


    @Override
    public Shop getCurrentShop() {
        Shop shop = currentShopContext.get();
        if (shop == null) {
            Long userId = SecurityUtil.getCurrentUserId();
            if (SecurityUtil.isAdmin()) {
                shop = shopMapper.selectDefault();
            } else if (userId != null) {
                List<Shop> shops = shopMapper.selectByUserId(userId);
                if (shops != null && !shops.isEmpty()) {
                    shop = shops.get(0);
                }
            }
            if (shop != null) {
                currentShopContext.set(shop);
            }
        }
        return shop;
    }

    @Override
    public ShopCredential getCurrentCredential() {
        ShopCredential credential = currentCredentialContext.get();
        if (credential == null) {
            Shop shop = getCurrentShop();
            if (shop != null) {
                credential = getCredential(shop.getId());
                currentCredentialContext.set(credential);
            }
        }
        return credential;
    }

    public void clearContext() {
        currentShopContext.remove();
        currentCredentialContext.remove();
    }

    private boolean canAccessShop(Long shopId) {
        if (shopId == null) {
            return false;
        }
        if (SecurityUtil.isAdmin()) {
            return true;
        }
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return false;
        }
        return userShopService.hasShopAccess(userId, shopId);
    }

    private void assertShopAccess(Long shopId) {
        if (shopId == null) {
            throw new AccessDeniedException("Shop access denied");
        }
        if (SecurityUtil.isAdmin()) {
            return;
        }
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null || !userShopService.hasShopAccess(userId, shopId)) {
            throw new AccessDeniedException("Shop access denied");
        }
    }

    private void decryptCredential(ShopCredential credential) {
        try {
            if (credential.getApiKeyEncrypted() != null) {
                credential.setApiKey(cryptoUtil.decrypt(credential.getApiKeyEncrypted()));
            }
            if (credential.getApiSecretEncrypted() != null) {
                credential.setApiSecret(cryptoUtil.decrypt(credential.getApiSecretEncrypted()));
            }
            if (credential.getAccessTokenEncrypted() != null) {
                credential.setAccessToken(cryptoUtil.decrypt(credential.getAccessTokenEncrypted()));
            }
            if (credential.getRefreshTokenEncrypted() != null) {
                credential.setRefreshToken(cryptoUtil.decrypt(credential.getRefreshTokenEncrypted()));
            }
        } catch (Exception e) {
            log.error("Failed to decrypt credential for shop: {}", credential.getShopId(), e);
        }
    }
}
