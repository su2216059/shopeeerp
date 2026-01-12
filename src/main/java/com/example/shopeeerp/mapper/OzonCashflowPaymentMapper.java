package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonCashflowPayment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OzonCashflowPaymentMapper {
    List<OzonCashflowPayment> selectByPeriodId(@Param("periodId") Long periodId);

    int insertBatch(@Param("list") List<OzonCashflowPayment> list);

    int deleteByPeriodId(@Param("periodId") Long periodId);

    int deleteAll();
}
