package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonCashflowOtherItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OzonCashflowOtherItemMapper {
    List<OzonCashflowOtherItem> selectByPeriodId(@Param("periodId") Long periodId);

    int insertBatch(@Param("list") List<OzonCashflowOtherItem> list);

    int deleteByPeriodId(@Param("periodId") Long periodId);

    int deleteAll();
}
