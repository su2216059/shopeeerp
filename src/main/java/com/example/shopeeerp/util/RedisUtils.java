package com.example.shopeeerp.util;

import java.time.Duration;

/**
 * @version: V1.0
 * @author: 苏航
 * @className: RedisUtils
 * @packageName: com.example.shopeeerp.util
 * @description:
 * @date: 2026/2/8 14:51
 */
public class RedisUtils {
    private static final String KEY_PREFIX = "ozon:purchasePrice:";
    private static final Duration TTL = Duration.ofMinutes(10);
    private static final Duration NULL_TTL = Duration.ofMinutes(2);

    public static String buildKey(Long shopId, String sku) {
        return KEY_PREFIX + shopId + ":" + sku;
    }

}
