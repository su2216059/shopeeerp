package com.example.shopeeerp.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Ozon商品主表实体类
 */
public class OzonProduct {
    private Long id;
    private String offerId;
    private String name;
    private Long sku;
    private Integer descriptionCategoryId;
    private Integer typeId;
    private String currencyCode;
    private BigDecimal price;
    private BigDecimal oldPrice;
    private String minPrice;
    private String vat;
    private BigDecimal volumeWeight;
    private Integer discountedFboStocks;
    private Boolean isArchived;
    private Boolean isAutoarchived;
    private Boolean isDiscounted;
    private Boolean isKgt;
    private Boolean isPrepaymentAllowed;
    private Boolean isSuper;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime syncTime;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSku() {
        return sku;
    }

    public void setSku(Long sku) {
        this.sku = sku;
    }

    public Integer getDescriptionCategoryId() {
        return descriptionCategoryId;
    }

    public void setDescriptionCategoryId(Integer descriptionCategoryId) {
        this.descriptionCategoryId = descriptionCategoryId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public BigDecimal getVolumeWeight() {
        return volumeWeight;
    }

    public void setVolumeWeight(BigDecimal volumeWeight) {
        this.volumeWeight = volumeWeight;
    }

    public Integer getDiscountedFboStocks() {
        return discountedFboStocks;
    }

    public void setDiscountedFboStocks(Integer discountedFboStocks) {
        this.discountedFboStocks = discountedFboStocks;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    public Boolean getIsAutoarchived() {
        return isAutoarchived;
    }

    public void setIsAutoarchived(Boolean isAutoarchived) {
        this.isAutoarchived = isAutoarchived;
    }

    public Boolean getIsDiscounted() {
        return isDiscounted;
    }

    public void setIsDiscounted(Boolean isDiscounted) {
        this.isDiscounted = isDiscounted;
    }

    public Boolean getIsKgt() {
        return isKgt;
    }

    public void setIsKgt(Boolean isKgt) {
        this.isKgt = isKgt;
    }

    public Boolean getIsPrepaymentAllowed() {
        return isPrepaymentAllowed;
    }

    public void setIsPrepaymentAllowed(Boolean isPrepaymentAllowed) {
        this.isPrepaymentAllowed = isPrepaymentAllowed;
    }

    public Boolean getIsSuper() {
        return isSuper;
    }

    public void setIsSuper(Boolean isSuper) {
        this.isSuper = isSuper;
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

    public LocalDateTime getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(LocalDateTime syncTime) {
        this.syncTime = syncTime;
    }

    @Override
    public String toString() {
        return "OzonProduct{" +
                "id=" + id +
                ", offerId='" + offerId + '\'' +
                ", name='" + name + '\'' +
                ", sku=" + sku +
                ", descriptionCategoryId=" + descriptionCategoryId +
                ", typeId=" + typeId +
                ", currencyCode='" + currencyCode + '\'' +
                ", price='" + price + '\'' +
                ", oldPrice='" + oldPrice + '\'' +
                ", minPrice='" + minPrice + '\'' +
                ", vat='" + vat + '\'' +
                ", volumeWeight=" + volumeWeight +
                ", discountedFboStocks=" + discountedFboStocks +
                ", isArchived=" + isArchived +
                ", isAutoarchived=" + isAutoarchived +
                ", isDiscounted=" + isDiscounted +
                ", isKgt=" + isKgt +
                ", isPrepaymentAllowed=" + isPrepaymentAllowed +
                ", isSuper=" + isSuper +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", syncTime=" + syncTime +
                '}';
    }
}