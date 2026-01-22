package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.ShopAccountMapper;
import com.example.shopeeerp.mapper.ShopCredentialMapper;
import com.example.shopeeerp.mapper.ShopMapper;
import com.example.shopeeerp.pojo.Shop;
import com.example.shopeeerp.pojo.ShopAccount;
import com.example.shopeeerp.pojo.ShopCredential;
import com.example.shopeeerp.service.ShopService;
import com.example.shopeeerp.util.CryptoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 店铺管理服务实现
 */
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

    // 当前店铺上下文 (线程安全)
    private static final ThreadLocal<Shop> currentShopContext = new ThreadLocal<>();
    private static final ThreadLocal<ShopCredential> currentCredentialContext = new ThreadLocal<>();

    // ========== 店铺管理 ==========

    @Override
    @Transactional
    public Shop createShop(Shop shop) {
        // 设置默认值
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
        log.info("Created shop: {} ({})", shop.getShopCode(), shop.getId());
        return shop;
    }

    @Override
    @Transactional
    public Shop updateShop(Shop shop) {
        shopMapper.update(shop);
        log.info("Updated shop: {}", shop.getShopCode());
        return shop;
    }

    @Override
    @Transactional
    public void deleteShop(Long shopId) {
        // 级联删除凭证和账号
        credentialMapper.deleteByShopId(shopId);
        accountMapper.deleteByShopId(shopId);
        shopMapper.deleteById(shopId);
        log.info("Deleted shop: {}", shopId);
    }

    @Override
    public Shop getShopById(Long shopId) {
        return shopMapper.selectById(shopId);
    }

    @Override
    public Shop getShopByCode(String shopCode) {
        return shopMapper.selectByCode(shopCode);
    }

    @Override
    public Shop getDefaultShop() {
        return shopMapper.selectDefault();
    }

    @Override
    public List<Shop> getShopsByUserId(Long userId) {
        return shopMapper.selectByUserId(userId);
    }

    @Override
    public List<Shop> getAllShops() {
        return shopMapper.selectAll();
    }

    @Override
    public List<Shop> getShopsByPlatform(String platform) {
        return shopMapper.selectByPlatform(platform);
    }

    // ========== API凭证管理 ==========

    @Override
    @Transactional
    public ShopCredential saveCredential(Long shopId, String clientId, String apiKey, String apiSecret) {
        ShopCredential credential = new ShopCredential();
        credential.setShopId(shopId);
        credential.setClientId(clientId);
        credential.setCredentialType("api_key");
        credential.setStatus("active");

        // 加密敏感字段
        if (apiKey != null && !apiKey.isEmpty()) {
            credential.setApiKeyEncrypted(cryptoUtil.encrypt(apiKey));
        }
        if (apiSecret != null && !apiSecret.isEmpty()) {
            credential.setApiSecretEncrypted(cryptoUtil.encrypt(apiSecret));
        }

        credentialMapper.upsert(credential);
        log.info("Saved credential for shop: {}, clientId: {}", shopId, clientId);

        // 返回解密后的凭证
        return getCredential(shopId);
    }

    @Override
    public ShopCredential getCredential(Long shopId) {
        ShopCredential credential = credentialMapper.selectByShopId(shopId);
        if (credential != null) {
            decryptCredential(credential);
        }
        return credential;
    }

    @Override
    public ShopCredential getCredentialByShopCode(String shopCode) {
        Shop shop = shopMapper.selectByCode(shopCode);
        if (shop == null) {
            return null;
        }
        return getCredential(shop.getId());
    }

    @Override
    public boolean verifyCredential(Long shopId) {
        ShopCredential credential = getCredential(shopId);
        if (credential == null || credential.getApiKey() == null) {
            return false;
        }

        // TODO: 调用平台API验证凭证有效性
        // 这里可以调用一个简单的API（如获取卖家信息）来验证

        credentialMapper.updateLastVerified(shopId);
        return true;
    }

    @Override
    public void updateCredentialStatus(Long shopId, String status) {
        credentialMapper.updateStatus(shopId, status);
    }

    @Override
    public void markCredentialUsed(Long shopId) {
        credentialMapper.updateLastUsed(shopId);
    }

    /**
     * 解密凭证中的敏感字段
     */
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

    // ========== 登录账号管理 ==========

    @Override
    @Transactional
    public ShopAccount addAccount(Long shopId, ShopAccount account) {
        account.setShopId(shopId);
        if (account.getStatus() == null) {
            account.setStatus("active");
        }

        // 加密密码
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
        // 如果提供了新密码，重新加密
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
        accountMapper.deleteById(accountId);
        log.info("Deleted account: {}", accountId);
    }

    @Override
    public List<ShopAccount> getAccountsByShopId(Long shopId) {
        List<ShopAccount> accounts = accountMapper.selectByShopId(shopId);
        // 不解密密码，只返回基本信息
        return accounts;
    }

    @Override
    public ShopAccount getAccountById(Long accountId) {
        ShopAccount account = accountMapper.selectById(accountId);
        if (account != null && account.getPasswordEncrypted() != null) {
            try {
                account.setPassword(cryptoUtil.decrypt(account.getPasswordEncrypted()));
            } catch (Exception e) {
                log.error("Failed to decrypt password for account: {}", accountId, e);
            }
        }
        return account;
    }

    // ========== 上下文切换 ==========

    @Override
    public void switchShop(Long shopId) {
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
            // 返回默认店铺
            shop = getDefaultShop();
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

    /**
     * 清除当前线程的店铺上下文
     */
    public void clearContext() {
        currentShopContext.remove();
        currentCredentialContext.remove();
    }
}
