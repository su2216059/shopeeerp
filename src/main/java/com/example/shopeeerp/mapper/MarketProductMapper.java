package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.MarketProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MarketProductMapper {

    MarketProduct selectByPlatformAndProductId(@Param("platform") String platform,
                                               @Param("platformProductId") String platformProductId);

    int upsert(MarketProduct product);
}
