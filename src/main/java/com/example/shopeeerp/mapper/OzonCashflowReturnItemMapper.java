package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonCashflowReturnItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OzonCashflowReturnItemMapper {
    List<OzonCashflowReturnItem> selectByPeriodId(@Param("periodId") Long periodId);

    int insertBatch(@Param("list") List<OzonCashflowReturnItem> list);

    int deleteByPeriodId(@Param("periodId") Long periodId);

    int deleteAll();
}
