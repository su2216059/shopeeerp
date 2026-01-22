package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.Shop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShopMapper {
    
    int insert(Shop shop);
    
    int update(Shop shop);
    
    int deleteById(@Param("id") Long id);
    
    Shop selectById(@Param("id") Long id);
    
    Shop selectByCode(@Param("shopCode") String shopCode);
    
    Shop selectDefault();
    
    List<Shop> selectByUserId(@Param("userId") Long userId);
    
    List<Shop> selectAll();
    
    List<Shop> selectByPlatform(@Param("platform") String platform);
    
    int updateDefault(@Param("id") Long id, @Param("isDefault") Boolean isDefault);
}
