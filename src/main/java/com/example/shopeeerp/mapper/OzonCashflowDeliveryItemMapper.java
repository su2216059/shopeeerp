package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonCashflowDeliveryItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OzonCashflowDeliveryItemMapper {
    List<OzonCashflowDeliveryItem> selectByPeriodId(@Param("periodId") Long periodId);

    int insertBatch(@Param("list") List<OzonCashflowDeliveryItem> list);

    int deleteByPeriodId(@Param("periodId") Long periodId);

    int deleteAll();
}
