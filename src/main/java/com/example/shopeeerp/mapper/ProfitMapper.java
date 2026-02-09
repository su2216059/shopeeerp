package com.example.shopeeerp.mapper;

import com.example.shopeeerp.adapter.dto.ozon.ProfitQueryDto;
import com.example.shopeeerp.adapter.dto.ozon.ProfitResultResponse;
import com.example.shopeeerp.pojo.ProfitOrderPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @version: V1.0
 * @author: 苏航
 * @className: ProfitMapper
 * @packageName: com.example.shopeeerp.mapper
 * @description:
 * @date: 2026/2/5 9:16
 */
@Mapper
public interface ProfitMapper {
    ProfitResultResponse summary(@Param("q") ProfitQueryDto q);

    int upsertProfitOrder(@Param("p") ProfitOrderPo profitOrderPo);
}
