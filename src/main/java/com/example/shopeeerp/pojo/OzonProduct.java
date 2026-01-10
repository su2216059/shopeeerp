package com.example.shopeeerp.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Ozon商品主表实体类
 */
public class OzonProduct {
    private Long id;                        // 商品ID
    private String offerId;                 // 商家SKU/货号
    private String name;                    // 商品名称
    private Long sku;                       // Ozon SKU
    private Integer descriptionCategoryId;  // 描述分类ID
    private Integer typeId;                 // 商品类型ID
    private String currencyCode;            // 货币代码
    private String price;                   // 当前价格
    private String oldPrice;                // 原价
    private String minPrice;                // 最低价格
    private String vat;                     // 增值税
    private BigDecimal volumeWeight;        // 体积重量
    private Integer discountedFboStocks;    // 折扣FBO库存数量
    private Boolean isArchived;             // 是否已归档
    private Boolean isAutoarchived;         // 是否自动归档
    private Boolean isDiscounted;           // 是否有折扣
    private Boolean isKgt;                  // 是否为大件商品(KGT)
    private Boolean isPrepaymentAllowed;    // 是否允许预付款
    private Boolean isSuper;                // 是否为超级商品
    private LocalDateTime createdAt;        // Ozon商品创建时间
    private LocalDateTime updatedAt;        // Ozon商品更新时间
    private LocalDateTime syncTime;         // 本地同步时间

    // 关联对象
    private List<OzonProductImage> images;          // 商品图片列表
    private OzonProductStatus status;               // 商品状态
    private OzonProductStockSummary stockSummary;   // 库存汇总
    private List<OzonProductStock> stocks;          // 库存明细列表

    public OzonProduct() {
    }

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
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

    public List<OzonProductImage> getImages() {
        return images;
    }

    public void setImages(List<OzonProductImage> images) {
        this.images = images;
    }

    public OzonProductStatus getStatus() {
        return status;
    }

    public void setStatus(OzonProductStatus status) {
        this.status = status;
    }

    public OzonProductStockSummary getStockSummary() {
        return stockSummary;
    }

    public void setStockSummary(OzonProductStockSummary stockSummary) {
        this.stockSummary = stockSummary;
    }

    public List<OzonProductStock> getStocks() {
        return stocks;
    }

    public void setStocks(List<OzonProductStock> stocks) {
        this.stocks = stocks;
    }
}
