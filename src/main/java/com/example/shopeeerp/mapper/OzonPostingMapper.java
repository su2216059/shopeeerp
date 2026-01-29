package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonPosting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface OzonPostingMapper {

    List<OzonPosting> selectAll();

    List<OzonPosting> selectByShopId(@Param("shopId") Long shopId);

    OzonPosting selectByPostingNumber(@Param("postingNumber") String postingNumber);

    OzonPosting selectByPostingNumberAndShopId(@Param("postingNumber") String postingNumber,
                                               @Param("shopId") Long shopId);

    int insert(OzonPosting posting);

    int insertBatch(@Param("list") List<OzonPosting> postings);

    int update(OzonPosting posting);

    int updatePurchaseAmount(@Param("postingNumber") String postingNumber,
                             @Param("purchaseAmount") BigDecimal purchaseAmount,
                             @Param("updatedAt") java.time.LocalDateTime updatedAt);

    int updatePurchaseAmountByShopId(@Param("postingNumber") String postingNumber,
                                     @Param("shopId") Long shopId,
                                     @Param("purchaseAmount") BigDecimal purchaseAmount,
                                     @Param("updatedAt") java.time.LocalDateTime updatedAt);

    int deleteByPostingNumber(@Param("postingNumber") String postingNumber);

    int deleteAll();
}
