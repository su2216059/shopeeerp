package com.example.shopeeerp.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ozon商品详情实体类
 */
public class ProductItem {
    private Long itemId;
    private Long ozonId;
    private String offerId;
    private Long sku;
    private String name;
    private BigDecimal price;
    private BigDecimal oldPrice;
    private BigDecimal minPrice;
    private String currencyCode;
    private Long descriptionCategoryId;
    private Long typeId;
    private String vat;
    private BigDecimal volumeWeight;
    private Boolean isArchived;
    private Boolean isAutoarchived;
    private Boolean isDiscounted;
    private Boolean isKgt;
    private Boolean isPrepaymentAllowed;
    private Boolean isSuper;
    private Boolean hasDiscountedFboItem;
    private Integer discountedFboStocks;
    private String status;
    private String moderateStatus;
    private String validationStatus;
    private String statusDescription;
    private String statusName;
    private String statusFailed;
    private Boolean isCreated;
    private String primaryImage;
    private String images;
    private String images360;
    private String colorImage;
    private String barcodes;
    private String stocksInfo;
    private String sourcesInfo;
    private String availabilitiesInfo;
    private String commissionsInfo;
    private String promotionsInfo;
    private String priceIndexesInfo;
    private String errorsInfo;
    private String modelInfo;
    private String visibilityDetails;
    private LocalDateTime ozonCreatedAt;
    private LocalDateTime ozonUpdatedAt;
    private LocalDateTime statusUpdatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductItem() {
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getOzonId() {
        return ozonId;
    }

    public void setOzonId(Long ozonId) {
        this.ozonId = ozonId;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public Long getSku() {
        return sku;
    }

    public void setSku(Long sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Long getDescriptionCategoryId() {
        return descriptionCategoryId;
    }

    public void setDescriptionCategoryId(Long descriptionCategoryId) {
        this.descriptionCategoryId = descriptionCategoryId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
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

    public Boolean getHasDiscountedFboItem() {
        return hasDiscountedFboItem;
    }

    public void setHasDiscountedFboItem(Boolean hasDiscountedFboItem) {
        this.hasDiscountedFboItem = hasDiscountedFboItem;
    }

    public Integer getDiscountedFboStocks() {
        return discountedFboStocks;
    }

    public void setDiscountedFboStocks(Integer discountedFboStocks) {
        this.discountedFboStocks = discountedFboStocks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getModerateStatus() {
        return moderateStatus;
    }

    public void setModerateStatus(String moderateStatus) {
        this.moderateStatus = moderateStatus;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusFailed() {
        return statusFailed;
    }

    public void setStatusFailed(String statusFailed) {
        this.statusFailed = statusFailed;
    }

    public Boolean getIsCreated() {
        return isCreated;
    }

    public void setIsCreated(Boolean isCreated) {
        this.isCreated = isCreated;
    }

    public String getPrimaryImage() {
        return primaryImage;
    }

    public void setPrimaryImage(String primaryImage) {
        this.primaryImage = primaryImage;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getImages360() {
        return images360;
    }

    public void setImages360(String images360) {
        this.images360 = images360;
    }

    public String getColorImage() {
        return colorImage;
    }

    public void setColorImage(String colorImage) {
        this.colorImage = colorImage;
    }

    public String getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(String barcodes) {
        this.barcodes = barcodes;
    }

    public String getStocksInfo() {
        return stocksInfo;
    }

    public void setStocksInfo(String stocksInfo) {
        this.stocksInfo = stocksInfo;
    }

    public String getSourcesInfo() {
        return sourcesInfo;
    }

    public void setSourcesInfo(String sourcesInfo) {
        this.sourcesInfo = sourcesInfo;
    }

    public String getAvailabilitiesInfo() {
        return availabilitiesInfo;
    }

    public void setAvailabilitiesInfo(String availabilitiesInfo) {
        this.availabilitiesInfo = availabilitiesInfo;
    }

    public String getCommissionsInfo() {
        return commissionsInfo;
    }

    public void setCommissionsInfo(String commissionsInfo) {
        this.commissionsInfo = commissionsInfo;
    }

    public String getPromotionsInfo() {
        return promotionsInfo;
    }

    public void setPromotionsInfo(String promotionsInfo) {
        this.promotionsInfo = promotionsInfo;
    }

    public String getPriceIndexesInfo() {
        return priceIndexesInfo;
    }

    public void setPriceIndexesInfo(String priceIndexesInfo) {
        this.priceIndexesInfo = priceIndexesInfo;
    }

    public String getErrorsInfo() {
        return errorsInfo;
    }

    public void setErrorsInfo(String errorsInfo) {
        this.errorsInfo = errorsInfo;
    }

    public String getModelInfo() {
        return modelInfo;
    }

    public void setModelInfo(String modelInfo) {
        this.modelInfo = modelInfo;
    }

    public String getVisibilityDetails() {
        return visibilityDetails;
    }

    public void setVisibilityDetails(String visibilityDetails) {
        this.visibilityDetails = visibilityDetails;
    }

    public LocalDateTime getOzonCreatedAt() {
        return ozonCreatedAt;
    }

    public void setOzonCreatedAt(LocalDateTime ozonCreatedAt) {
        this.ozonCreatedAt = ozonCreatedAt;
    }

    public LocalDateTime getOzonUpdatedAt() {
        return ozonUpdatedAt;
    }

    public void setOzonUpdatedAt(LocalDateTime ozonUpdatedAt) {
        this.ozonUpdatedAt = ozonUpdatedAt;
    }

    public LocalDateTime getStatusUpdatedAt() {
        return statusUpdatedAt;
    }

    public void setStatusUpdatedAt(LocalDateTime statusUpdatedAt) {
        this.statusUpdatedAt = statusUpdatedAt;
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
