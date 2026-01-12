package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonCashflowPeriod;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OzonCashflowPeriodMapper {
    List<OzonCashflowPeriod> selectAll();

    OzonCashflowPeriod selectById(@Param("id") Long id);

    int insert(OzonCashflowPeriod period);

    int update(OzonCashflowPeriod period);

    int deleteById(@Param("id") Long id);

    int deleteAll();
}
