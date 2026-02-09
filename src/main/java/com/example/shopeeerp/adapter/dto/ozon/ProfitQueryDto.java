package com.example.shopeeerp.adapter.dto.ozon;

import lombok.Data;

import java.time.LocalDate;

/**
 * @version: V1.0
 * @author: 苏航
 * @className: ProfitQueryDto
 * @packageName: com.example.shopeeerp.adapter.dto.ozon
 * @description:
 * @date: 2026/2/4 16:30
 */
@Data
public class ProfitQueryDto {
    Long shopId;
    /** yyyy-MM-dd */
    LocalDate startDate;
    /** yyyy-MM-dd */
    LocalDate endDate;
    String sku;

    /** 可选：按订单维度查询 */
    private String orderId;

}
