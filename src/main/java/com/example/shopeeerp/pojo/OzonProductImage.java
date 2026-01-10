package com.example.shopeeerp.pojo;

import java.time.LocalDateTime;

/**
 * Ozon商品图片实体类
 */
public class OzonProductImage {
    private Long id;                // 自增主键
    private Long productId;         // 商品ID
    private String imageUrl;        // 图片URL
    private Integer sortOrder;      // 排序顺序
    private Boolean isPrimary;      // 是否为主图
    private LocalDateTime createdAt; // 创建时间

    public OzonProductImage() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
