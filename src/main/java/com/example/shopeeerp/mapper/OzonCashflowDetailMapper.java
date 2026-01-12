package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonCashflowDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OzonCashflowDetailMapper {
    OzonCashflowDetail selectByPeriodId(@Param("periodId") Long periodId);

    int insert(OzonCashflowDetail detail);

    int update(OzonCashflowDetail detail);

    int deleteByPeriodId(@Param("periodId") Long periodId);

    int deleteAll();
}
