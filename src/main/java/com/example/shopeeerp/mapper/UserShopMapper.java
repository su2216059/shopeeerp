package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.UserShop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserShopMapper {
    int insert(UserShop userShop);

    int deleteByUserIdAndShopId(@Param("userId") Long userId, @Param("shopId") Long shopId);

    int countByUserIdAndShopId(@Param("userId") Long userId, @Param("shopId") Long shopId);

    List<Long> selectShopIdsByUserId(@Param("userId") Long userId);

    List<UserShop> selectByUserId(@Param("userId") Long userId);
}
