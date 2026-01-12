package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonCashflowServiceItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OzonCashflowServiceItemMapper {
    List<OzonCashflowServiceItem> selectByPeriodId(@Param("periodId") Long periodId);

    int insertBatch(@Param("list") List<OzonCashflowServiceItem> list);

    int deleteByPeriodId(@Param("periodId") Long periodId);

    int deleteAll();
}
