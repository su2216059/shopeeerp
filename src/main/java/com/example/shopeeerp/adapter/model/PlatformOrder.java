package com.example.shopeeerp.adapter.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 平台订单模型（平台无关）
 */
public class PlatformOrder {
    private String orderId;
    private String platformOrderId;
    private String customerId;
    private String customerName;
    private String status;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String shippingStatus;
    private LocalDateTime orderDate;
    private List<PlatformOrderItem> items;
    
    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getPlatformOrderId() {
        return platformOrderId;
    }
    
    public void setPlatformOrderId(String platformOrderId) {
        this.platformOrderId = platformOrderId;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getShippingStatus() {
        return shippingStatus;
    }
    
    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public List<PlatformOrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<PlatformOrderItem> items) {
        this.items = items;
    }
}
