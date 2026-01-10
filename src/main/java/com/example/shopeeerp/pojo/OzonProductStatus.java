package com.example.shopeeerp.pojo;

import java.util.Date;

/**
 * Ozon商品状态表实体类
 */
public class OzonProductStatus {
    private Long id;
    private Long productId;
    private Boolean isCreated;
    private String moderateStatus;
    private String status;
    private String statusDescription;
    private String statusFailed;
    private String statusName;
    private String statusTooltip;
    private Date statusUpdatedAt;
    private String validationStatus;
    private Date createdAt;
    private Date updatedAt;

    // Getters and Setters
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

    public Date getStatusUpdatedAt() {
        return statusUpdatedAt;
    }

    public void setStatusUpdatedAt(Date statusUpdatedAt) {
        this.statusUpdatedAt = statusUpdatedAt;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "OzonProductStatus{" +
                "id=" + id +
                ", productId=" + productId +
                ", isCreated=" + isCreated +
                ", moderateStatus='" + moderateStatus + '\'' +
                ", status='" + status + '\'' +
                ", statusDescription='" + statusDescription + '\'' +
                ", statusFailed='" + statusFailed + '\'' +
                ", statusName='" + statusName + '\'' +
                ", statusTooltip='" + statusTooltip + '\'' +
                ", statusUpdatedAt=" + statusUpdatedAt +
                ", validationStatus='" + validationStatus + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}