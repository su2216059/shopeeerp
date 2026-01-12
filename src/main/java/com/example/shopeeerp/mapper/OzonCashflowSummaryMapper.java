package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonCashflowSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OzonCashflowSummaryMapper {
    List<OzonCashflowSummary> selectByPeriodId(@Param("periodId") Long periodId);

    int insertBatch(@Param("list") List<OzonCashflowSummary> list);

    int deleteByPeriodId(@Param("periodId") Long periodId);

    int deleteAll();
}
