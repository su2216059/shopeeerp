package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.Shop;
import com.example.shopeeerp.pojo.ShopAccount;
import com.example.shopeeerp.pojo.ShopCredential;

import java.util.List;

/**
 * 店铺管理服务接口
 */
public interface ShopService {
    
    // ========== 店铺管理 ==========
    
    /**
     * 创建店铺
     */
    Shop createShop(Shop shop);
    
    /**
     * 更新店铺信息
     */
    Shop updateShop(Shop shop);
    
    /**
     * 删除店铺
     */
    void deleteShop(Long shopId);
    
    /**
     * 获取店铺详情
     */
    Shop getShopById(Long shopId);
    
    /**
     * 根据店铺编码获取
     */
    Shop getShopByCode(String shopCode);
    
    /**
     * 获取默认店铺
     */
    Shop getDefaultShop();
    
    /**
     * 获取用户的所有店铺
     */
    List<Shop> getShopsByUserId(Long userId);
    
    /**
     * 获取所有店铺
     */
    List<Shop> getAllShops();
    
    /**
     * 按平台获取店铺
     */
    List<Shop> getShopsByPlatform(String platform);
    
    // ========== API凭证管理 ==========
    
    /**
     * 保存/更新API凭证 (会自动加密)
     */
    ShopCredential saveCredential(Long shopId, String clientId, String apiKey, String apiSecret);
    
    /**
     * 获取店铺的API凭证 (会自动解密)
     */
    ShopCredential getCredential(Long shopId);
    
    /**
     * 根据店铺编码获取凭证 (会自动解密)
     */
    ShopCredential getCredentialByShopCode(String shopCode);
    
    /**
     * 验证凭证是否有效
     */
    boolean verifyCredential(Long shopId);
    
    /**
     * 更新凭证状态
     */
    void updateCredentialStatus(Long shopId, String status);
    
    /**
     * 记录凭证最后使用时间
     */
    void markCredentialUsed(Long shopId);
    
    // ========== 登录账号管理 ==========
    
    /**
     * 添加登录账号
     */
    ShopAccount addAccount(Long shopId, ShopAccount account);
    
    /**
     * 更新登录账号
     */
    ShopAccount updateAccount(ShopAccount account);
    
    /**
     * 删除登录账号
     */
    void deleteAccount(Long accountId);
    
    /**
     * 获取店铺的所有账号
     */
    List<ShopAccount> getAccountsByShopId(Long shopId);
    
    /**
     * 获取账号详情 (会解密密码)
     */
    ShopAccount getAccountById(Long accountId);
    
    // ========== 上下文切换 ==========
    
    /**
     * 切换当前操作的店铺
     * (用于多店铺场景下切换API调用的目标店铺)
     */
    void switchShop(Long shopId);
    
    /**
     * 获取当前店铺
     */
    Shop getCurrentShop();
    
    /**
     * 获取当前店铺的凭证
     */
    ShopCredential getCurrentCredential();
}
