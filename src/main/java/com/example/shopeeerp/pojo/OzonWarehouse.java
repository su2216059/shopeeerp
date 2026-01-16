package com.example.shopeeerp.pojo;

import java.time.LocalDateTime;

public class OzonWarehouse {
    private Long warehouseId;
    private String name;
    private String storeName;
    private String status;
    private String workingDays;
    private Integer minWorkingDays;
    private Integer postingsLimit;
    private Integer minPostingsLimit;
    private Boolean isRfbs;
    private Boolean hasEntrustedAcceptance;
    private Boolean canPrintActInAdvance;
    private Boolean hasPostingsLimit;
    private Boolean isKarantin;
    private Boolean isKgt;
    private Boolean isTimetableEditable;
    private String firstMileTypeJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(String workingDays) {
        this.workingDays = workingDays;
    }

    public Integer getMinWorkingDays() {
        return minWorkingDays;
    }

    public void setMinWorkingDays(Integer minWorkingDays) {
        this.minWorkingDays = minWorkingDays;
    }

    public Integer getPostingsLimit() {
        return postingsLimit;
    }

    public void setPostingsLimit(Integer postingsLimit) {
        this.postingsLimit = postingsLimit;
    }

    public Integer getMinPostingsLimit() {
        return minPostingsLimit;
    }

    public void setMinPostingsLimit(Integer minPostingsLimit) {
        this.minPostingsLimit = minPostingsLimit;
    }

    public Boolean getIsRfbs() {
        return isRfbs;
    }

    public void setIsRfbs(Boolean isRfbs) {
        this.isRfbs = isRfbs;
    }

    public Boolean getHasEntrustedAcceptance() {
        return hasEntrustedAcceptance;
    }

    public void setHasEntrustedAcceptance(Boolean hasEntrustedAcceptance) {
        this.hasEntrustedAcceptance = hasEntrustedAcceptance;
    }

    public Boolean getCanPrintActInAdvance() {
        return canPrintActInAdvance;
    }

    public void setCanPrintActInAdvance(Boolean canPrintActInAdvance) {
        this.canPrintActInAdvance = canPrintActInAdvance;
    }

    public Boolean getHasPostingsLimit() {
        return hasPostingsLimit;
    }

    public void setHasPostingsLimit(Boolean hasPostingsLimit) {
        this.hasPostingsLimit = hasPostingsLimit;
    }

    public Boolean getIsKarantin() {
        return isKarantin;
    }

    public void setIsKarantin(Boolean isKarantin) {
        this.isKarantin = isKarantin;
    }

    public Boolean getIsKgt() {
        return isKgt;
    }

    public void setIsKgt(Boolean isKgt) {
        this.isKgt = isKgt;
    }

    public Boolean getIsTimetableEditable() {
        return isTimetableEditable;
    }

    public void setIsTimetableEditable(Boolean isTimetableEditable) {
        this.isTimetableEditable = isTimetableEditable;
    }

    public String getFirstMileTypeJson() {
        return firstMileTypeJson;
    }

    public void setFirstMileTypeJson(String firstMileTypeJson) {
        this.firstMileTypeJson = firstMileTypeJson;
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