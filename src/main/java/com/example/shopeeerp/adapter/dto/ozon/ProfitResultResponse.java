package com.example.shopeeerp.adapter.dto.ozon;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @version: V1.0
 * @author: 苏航
 * @className: ProfitResultResponse
 * @packageName: com.example.shopeeerp.adapter.dto.ozon
 * @description:
 * @date: 2026/2/4 16:31
 */
@Data
public class ProfitResultResponse {
    //营业额
    private BigDecimal revenue;
    //成本
    private BigDecimal cost;
    //平台费用
    private BigDecimal platformFee;
    //利润
    private BigDecimal profit;
    //利润率
    private BigDecimal profitRate;

}
