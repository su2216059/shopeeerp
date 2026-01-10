package com.example.shopeeerp.pojo;

import java.time.LocalDateTime;

/**
 * Ozon商品状态实体类
 */
public class OzonProductStatus {
    private Long id;                        // 自增主键
    private Long productId;                 // 商品ID
    private Boolean isCreated;              // 是否已创建
    private String moderateStatus;          // 审核状态
    private String status;                  // 商品状态
    private String statusDescription;       // 状态描述
    private String statusFailed;            // 失败状态说明
    private String statusName;              // 状态名称
    private String statusTooltip;           // 状态提示信息
    private LocalDateTime statusUpdatedAt;  // 状态更新时间
    private String validationStatus;        // 验证状态
    private LocalDateTime createdAt;        // 创建时间
    private LocalDateTime updatedAt;        // 更新时间

    public OzonProductStatus() {
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

    public Boolean getIsCreated() {
        return isCreated;
    }

    public void setIsCreated(Boolean isCreated) {
        this.isCreated = isCreated;
    }

    public String getModerateStatus() {
        return moderateStatus;
    }

    public void setModerateStatus(String moderateStatus) {
        this.moderateStatus = moderateStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getStatusFailed() {
        return statusFailed;
    }

    public void setStatusFailed(String statusFailed) {
        this.statusFailed = statusFailed;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusTooltip() {
        return statusTooltip;
    }

    public void setStatusTooltip(String statusTooltip) {
        this.statusTooltip = statusTooltip;
    }

    public LocalDateTime getStatusUpdatedAt() {
        return statusUpdatedAt;
    }

    public void setStatusUpdatedAt(LocalDateTime statusUpdatedAt) {
        this.statusUpdatedAt = statusUpdatedAt;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
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
