package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.MarketProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MarketProductMapper {

    MarketProduct selectByPlatformAndProductId(@Param("platform") String platform,
                                               @Param("platformProductId") String platformProductId);

    int upsert(MarketProduct product);

    /**
     * 分页查询商品列表
     */
    List<MarketProduct> selectList(@Param("platform") String platform,
                                   @Param("keyword") String keyword,
                                   @Param("brand") String brand,
                                   @Param("categoryId") String categoryId,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);

    /**
     * 统计商品数量
     */
    int countList(@Param("platform") String platform,
                  @Param("keyword") String keyword,
                  @Param("brand") String brand,
                  @Param("categoryId") String categoryId);

    /**
     * 获取所有品牌（去重）
     */
    List<String> selectDistinctBrands(@Param("platform") String platform);

    /**
     * 获取所有分类（去重）
     */
    List<String> selectDistinctCategories(@Param("platform") String platform);
}
