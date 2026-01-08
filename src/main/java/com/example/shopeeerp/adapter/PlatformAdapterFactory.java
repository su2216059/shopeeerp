package com.example.shopeeerp.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 平台适配器工厂
 * 用于管理和获取不同平台的适配器
 */
@Component
public class PlatformAdapterFactory {
    
    private final Map<String, PlatformAdapter> adapters = new HashMap<>();
    
    @Autowired
    public PlatformAdapterFactory(List<PlatformAdapter> adapterList) {
        for (PlatformAdapter adapter : adapterList) {
            adapters.put(adapter.getPlatformName().toLowerCase(), adapter);
        }
    }
    
    /**
     * 根据平台名称获取适配器
     * @param platformName 平台名称（不区分大小写）
     * @return 平台适配器
     * @throws IllegalArgumentException 如果平台不存在
     */
    public PlatformAdapter getAdapter(String platformName) {
        PlatformAdapter adapter = adapters.get(platformName.toLowerCase());
        if (adapter == null) {
            throw new IllegalArgumentException("不支持的平台: " + platformName);
        }
        return adapter;
    }
    
    /**
     * 获取所有支持的平台名称
     * @return 平台名称列表
     */
    public String[] getSupportedPlatforms() {
        return adapters.keySet().toArray(new String[0]);
    }
    
    /**
     * 检查平台是否支持
     * @param platformName 平台名称
     * @return 是否支持
     */
    public boolean isSupported(String platformName) {
        return adapters.containsKey(platformName.toLowerCase());
    }
}
