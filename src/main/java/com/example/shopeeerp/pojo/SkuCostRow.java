package com.example.shopeeerp.pojo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @version: V1.0
 * @author: 苏航
 * @className: SkuCostRow
 * @packageName: com.example.shopeeerp.pojo
 * @description:
 * @date: 2026/2/5 11:46
 */
@Data
public class SkuCostRow {
    private String sku;
    private BigDecimal purchasePrice;
}