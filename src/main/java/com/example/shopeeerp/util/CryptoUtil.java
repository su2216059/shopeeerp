package com.example.shopeeerp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 加密工具类
 * 使用 AES-GCM 加密敏感数据
 */
@Component
public class CryptoUtil {
    
    private static final Logger log = LoggerFactory.getLogger(CryptoUtil.class);
    
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    
    @Value("${app.crypto.secret-key:default-secret-key-32chars!!}")
    private String secretKeyString;
    
    private SecretKeySpec secretKey;
    private SecureRandom secureRandom;
    
    @PostConstruct
    public void init() {
        // 确保密钥是32字节 (256位)
        byte[] keyBytes = new byte[32];
        byte[] providedKey = secretKeyString.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(providedKey, 0, keyBytes, 0, Math.min(providedKey.length, 32));
        
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
        this.secureRandom = new SecureRandom();
        
        log.info("CryptoUtil initialized");
    }
    
    /**
     * 加密字符串
     * @param plainText 明文
     * @return Base64编码的密文 (格式: IV + 密文)
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return null;
        }
        
        try {
            // 生成随机 IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // 将 IV 和密文组合
            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            log.error("Encryption failed", e);
            throw new RuntimeException("Encryption failed", e);
        }
    }
    
    /**
     * 解密字符串
     * @param encryptedText Base64编码的密文
     * @return 明文
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return null;
        }
        
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedText);
            
            // 提取 IV 和密文
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] cipherText = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, cipherText, 0, cipherText.length);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            
            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Decryption failed", e);
            throw new RuntimeException("Decryption failed", e);
        }
    }
    
    /**
     * 掩码显示 (用于日志和前端展示)
     * 例如: "abcdefghij" -> "abc****hij"
     */
    public static String mask(String text) {
        if (text == null || text.length() <= 6) {
            return "****";
        }
        int showLen = 3;
        return text.substring(0, showLen) + "****" + text.substring(text.length() - showLen);
    }
}
