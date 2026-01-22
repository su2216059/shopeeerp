package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.ShopAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShopAccountMapper {
    
    int insert(ShopAccount account);
    
    int update(ShopAccount account);
    
    int deleteById(@Param("id") Long id);
    
    int deleteByShopId(@Param("shopId") Long shopId);
    
    ShopAccount selectById(@Param("id") Long id);
    
    List<ShopAccount> selectByShopId(@Param("shopId") Long shopId);
    
    List<ShopAccount> selectByShopIdAndType(@Param("shopId") Long shopId, @Param("accountType") String accountType);
    
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    int updateLastLogin(@Param("id") Long id);
    
    int incrementLoginFail(@Param("id") Long id);
    
    int resetLoginFail(@Param("id") Long id);
}
