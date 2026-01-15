package com.example.shopeeerp.pojo;

import java.time.LocalDateTime;

/**
 * Ozon FBS 货件（posting）实体
 */
public class OzonPosting {
    private String postingNumber;
    private Long shopId;
    private Long orderId;
    private String orderNumber;
    private String status;
    private String substatus;
    private String tplIntegrationType;
    private Long deliveryMethodId;
    private String deliveryMethodName;
    private Long warehouseId;
    private String warehouseName;
    private Long tplProviderId;
    private String tplProviderName;
    private String trackingNumber;
    private LocalDateTime inProcessAt;
    private LocalDateTime shipmentDate;
    private LocalDateTime shipmentDateWithoutDelay;
    private LocalDateTime deliveringDate;
    private Boolean isExpress;
    private String customerJson;
    private String addresseeJson;
    private String barcodesJson;
    private String analyticsJson;
    private String financialJson;
    private String optionalJson;
    private String cancellationJson;
    private String requirementsJson;
    private String tarifficationJson;
    private String availableActions;
    private LocalDateTime lastChangedAt;
    private String rawPayload;
    private java.math.BigDecimal purchaseAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getPostingNumber() {
        return postingNumber;
    }

    public void setPostingNumber(String postingNumber) {
        this.postingNumber = postingNumber;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubstatus() {
        return substatus;
    }

    public void setSubstatus(String substatus) {
        this.substatus = substatus;
    }

    public String getTplIntegrationType() {
        return tplIntegrationType;
    }

    public void setTplIntegrationType(String tplIntegrationType) {
        this.tplIntegrationType = tplIntegrationType;
    }

    public Long getDeliveryMethodId() {
        return deliveryMethodId;
    }

    public void setDeliveryMethodId(Long deliveryMethodId) {
        this.deliveryMethodId = deliveryMethodId;
    }

    public String getDeliveryMethodName() {
        return deliveryMethodName;
    }

    public void setDeliveryMethodName(String deliveryMethodName) {
        this.deliveryMethodName = deliveryMethodName;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Long getTplProviderId() {
        return tplProviderId;
    }

    public void setTplProviderId(Long tplProviderId) {
        this.tplProviderId = tplProviderId;
    }

    public String getTplProviderName() {
        return tplProviderName;
    }

    public void setTplProviderName(String tplProviderName) {
        this.tplProviderName = tplProviderName;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public LocalDateTime getInProcessAt() {
        return inProcessAt;
    }

    public void setInProcessAt(LocalDateTime inProcessAt) {
        this.inProcessAt = inProcessAt;
    }

    public LocalDateTime getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(LocalDateTime shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public LocalDateTime getShipmentDateWithoutDelay() {
        return shipmentDateWithoutDelay;
    }

    public void setShipmentDateWithoutDelay(LocalDateTime shipmentDateWithoutDelay) {
        this.shipmentDateWithoutDelay = shipmentDateWithoutDelay;
    }

    public LocalDateTime getDeliveringDate() {
        return deliveringDate;
    }

    public void setDeliveringDate(LocalDateTime deliveringDate) {
        this.deliveringDate = deliveringDate;
    }

    public Boolean getIsExpress() {
        return isExpress;
    }

    public void setIsExpress(Boolean isExpress) {
        this.isExpress = isExpress;
    }

    public String getCustomerJson() {
        return customerJson;
    }

    public void setCustomerJson(String customerJson) {
        this.customerJson = customerJson;
    }

    public String getAddresseeJson() {
        return addresseeJson;
    }

    public void setAddresseeJson(String addresseeJson) {
        this.addresseeJson = addresseeJson;
    }

    public String getBarcodesJson() {
        return barcodesJson;
    }

    public void setBarcodesJson(String barcodesJson) {
        this.barcodesJson = barcodesJson;
    }

    public String getAnalyticsJson() {
        return analyticsJson;
    }

    public void setAnalyticsJson(String analyticsJson) {
        this.analyticsJson = analyticsJson;
    }

    public String getFinancialJson() {
        return financialJson;
    }

    public void setFinancialJson(String financialJson) {
        this.financialJson = financialJson;
    }

    public String getOptionalJson() {
        return optionalJson;
    }

    public void setOptionalJson(String optionalJson) {
        this.optionalJson = optionalJson;
    }

    public String getCancellationJson() {
        return cancellationJson;
    }

    public void setCancellationJson(String cancellationJson) {
        this.cancellationJson = cancellationJson;
    }

    public String getRequirementsJson() {
        return requirementsJson;
    }

    public void setRequirementsJson(String requirementsJson) {
        this.requirementsJson = requirementsJson;
    }

    public String getTarifficationJson() {
        return tarifficationJson;
    }

    public void setTarifficationJson(String tarifficationJson) {
        this.tarifficationJson = tarifficationJson;
    }

    public String getAvailableActions() {
        return availableActions;
    }

    public void setAvailableActions(String availableActions) {
        this.availableActions = availableActions;
    }

    public LocalDateTime getLastChangedAt() {
        return lastChangedAt;
    }

    public void setLastChangedAt(LocalDateTime lastChangedAt) {
        this.lastChangedAt = lastChangedAt;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

    public java.math.BigDecimal getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(java.math.BigDecimal purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
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
