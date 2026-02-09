package com.example.shopeeerp.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @version: V1.0
 * @author: 苏航
 * @className: ProfitOrderPo
 * @packageName: com.example.shopeeerp.pojo
 * @description:
 * @date: 2026/2/5 10:13
 */
@Data
public class ProfitOrderPo {
    private Long shopId;
    private String orderId;
    private String sku;

    private BigDecimal revenue;
    private BigDecimal cost;
    private BigDecimal platformFee;
    private BigDecimal profit;
    private BigDecimal profitRate;

    private String currency;
    private LocalDate bizDate;
}
