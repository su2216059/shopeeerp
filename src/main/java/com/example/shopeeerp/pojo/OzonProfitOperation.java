package com.example.shopeeerp.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ozon 财报利润/费用明细（来自 finance cash-flow-statement）。
 */
public class OzonProfitOperation {
    private Long id;
    private Long operationId;
    private String operationType;
    private String operationTypeName;
    private LocalDateTime operationDate;
    private BigDecimal deliveryCharge;
    private BigDecimal returnDeliveryCharge;
    private BigDecimal accrualsForSale;
    private BigDecimal saleCommission;
    private BigDecimal amount;
    private BigDecimal purchaseCost;
    private String type;
    private String deliverySchema;
    private LocalDateTime orderDate;
    private String postingNumber;
    private Long warehouseId;
    /**
     * 原始 items json（结构随接口调整）
     */
    private String rawItemsJson;
    /**
     * 原始 services json（结构随接口调整）
     */
    private String rawServicesJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationTypeName() {
        return operationTypeName;
    }

    public void setOperationTypeName(String operationTypeName) {
        this.operationTypeName = operationTypeName;
    }

    public LocalDateTime getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(LocalDateTime operationDate) {
        this.operationDate = operationDate;
    }

    public BigDecimal getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(BigDecimal deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public BigDecimal getReturnDeliveryCharge() {
        return returnDeliveryCharge;
    }

    public void setReturnDeliveryCharge(BigDecimal returnDeliveryCharge) {
        this.returnDeliveryCharge = returnDeliveryCharge;
    }

    public BigDecimal getAccrualsForSale() {
        return accrualsForSale;
    }

    public void setAccrualsForSale(BigDecimal accrualsForSale) {
        this.accrualsForSale = accrualsForSale;
    }

    public BigDecimal getSaleCommission() {
        return saleCommission;
    }

    public void setSaleCommission(BigDecimal saleCommission) {
        this.saleCommission = saleCommission;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPurchaseCost() {
        return purchaseCost;
    }

    public void setPurchaseCost(BigDecimal purchaseCost) {
        this.purchaseCost = purchaseCost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeliverySchema() {
        return deliverySchema;
    }

    public void setDeliverySchema(String deliverySchema) {
        this.deliverySchema = deliverySchema;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getPostingNumber() {
        return postingNumber;
    }

    public void setPostingNumber(String postingNumber) {
        this.postingNumber = postingNumber;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getRawItemsJson() {
        return rawItemsJson;
    }

    public void setRawItemsJson(String rawItemsJson) {
        this.rawItemsJson = rawItemsJson;
    }

    public String getRawServicesJson() {
        return rawServicesJson;
    }

    public void setRawServicesJson(String rawServicesJson) {
        this.rawServicesJson = rawServicesJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
