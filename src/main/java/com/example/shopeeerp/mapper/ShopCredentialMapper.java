package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.ShopCredential;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ShopCredentialMapper {
    
    int insert(ShopCredential credential);
    
    int update(ShopCredential credential);
    
    int upsert(ShopCredential credential);
    
    int deleteByShopId(@Param("shopId") Long shopId);
    
    ShopCredential selectByShopId(@Param("shopId") Long shopId);
    
    int updateStatus(@Param("shopId") Long shopId, @Param("status") String status);
    
    int updateLastUsed(@Param("shopId") Long shopId);
    
    int updateLastVerified(@Param("shopId") Long shopId);
}
