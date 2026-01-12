package com.example.shopeeerp.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OzonCashflowSummary {
    private Long id;
    private Long periodId;
    private BigDecimal commissionAmount;
    private String currencyCode;
    private BigDecimal itemDeliveryReturnAmount;
    private BigDecimal ordersAmount;
    private BigDecimal returnsAmount;
    private BigDecimal servicesAmount;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getItemDeliveryReturnAmount() {
        return itemDeliveryReturnAmount;
    }

    public void setItemDeliveryReturnAmount(BigDecimal itemDeliveryReturnAmount) {
        this.itemDeliveryReturnAmount = itemDeliveryReturnAmount;
    }

    public BigDecimal getOrdersAmount() {
        return ordersAmount;
    }

    public void setOrdersAmount(BigDecimal ordersAmount) {
        this.ordersAmount = ordersAmount;
    }

    public BigDecimal getReturnsAmount() {
        return returnsAmount;
    }

    public void setReturnsAmount(BigDecimal returnsAmount) {
        this.returnsAmount = returnsAmount;
    }

    public BigDecimal getServicesAmount() {
        return servicesAmount;
    }

    public void setServicesAmount(BigDecimal servicesAmount) {
        this.servicesAmount = servicesAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
