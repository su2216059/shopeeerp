package com.example.shopeeerp.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OzonCashflowDetail {
    private Long periodId;
    private BigDecimal beginBalanceAmount;
    private BigDecimal deliveryTotal;
    private BigDecimal deliveryAmount;
    private BigDecimal returnTotal;
    private BigDecimal returnAmount;
    private BigDecimal loan;
    private BigDecimal invoiceTransfer;
    private BigDecimal rfbsTotal;
    private BigDecimal rfbsTransferDelivery;
    private BigDecimal rfbsTransferDeliveryReturn;
    private BigDecimal rfbsCompensationDeliveryReturn;
    private BigDecimal rfbsPartialCompensation;
    private BigDecimal rfbsPartialCompensationReturn;
    private BigDecimal servicesTotal;
    private BigDecimal othersTotal;
    private BigDecimal endBalanceAmount;
    private LocalDateTime createdAt;

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public BigDecimal getBeginBalanceAmount() {
        return beginBalanceAmount;
    }

    public void setBeginBalanceAmount(BigDecimal beginBalanceAmount) {
        this.beginBalanceAmount = beginBalanceAmount;
    }

    public BigDecimal getDeliveryTotal() {
        return deliveryTotal;
    }

    public void setDeliveryTotal(BigDecimal deliveryTotal) {
        this.deliveryTotal = deliveryTotal;
    }

    public BigDecimal getDeliveryAmount() {
        return deliveryAmount;
    }

    public void setDeliveryAmount(BigDecimal deliveryAmount) {
        this.deliveryAmount = deliveryAmount;
    }

    public BigDecimal getReturnTotal() {
        return returnTotal;
    }

    public void setReturnTotal(BigDecimal returnTotal) {
        this.returnTotal = returnTotal;
    }

    public BigDecimal getReturnAmount() {
        return returnAmount;
    }

    public void setReturnAmount(BigDecimal returnAmount) {
        this.returnAmount = returnAmount;
    }

    public BigDecimal getLoan() {
        return loan;
    }

    public void setLoan(BigDecimal loan) {
        this.loan = loan;
    }

    public BigDecimal getInvoiceTransfer() {
        return invoiceTransfer;
    }

    public void setInvoiceTransfer(BigDecimal invoiceTransfer) {
        this.invoiceTransfer = invoiceTransfer;
    }

    public BigDecimal getRfbsTotal() {
        return rfbsTotal;
    }

    public void setRfbsTotal(BigDecimal rfbsTotal) {
        this.rfbsTotal = rfbsTotal;
    }

    public BigDecimal getRfbsTransferDelivery() {
        return rfbsTransferDelivery;
    }

    public void setRfbsTransferDelivery(BigDecimal rfbsTransferDelivery) {
        this.rfbsTransferDelivery = rfbsTransferDelivery;
    }

    public BigDecimal getRfbsTransferDeliveryReturn() {
        return rfbsTransferDeliveryReturn;
    }

    public void setRfbsTransferDeliveryReturn(BigDecimal rfbsTransferDeliveryReturn) {
        this.rfbsTransferDeliveryReturn = rfbsTransferDeliveryReturn;
    }

    public BigDecimal getRfbsCompensationDeliveryReturn() {
        return rfbsCompensationDeliveryReturn;
    }

    public void setRfbsCompensationDeliveryReturn(BigDecimal rfbsCompensationDeliveryReturn) {
        this.rfbsCompensationDeliveryReturn = rfbsCompensationDeliveryReturn;
    }

    public BigDecimal getRfbsPartialCompensation() {
        return rfbsPartialCompensation;
    }

    public void setRfbsPartialCompensation(BigDecimal rfbsPartialCompensation) {
        this.rfbsPartialCompensation = rfbsPartialCompensation;
    }

    public BigDecimal getRfbsPartialCompensationReturn() {
        return rfbsPartialCompensationReturn;
    }

    public void setRfbsPartialCompensationReturn(BigDecimal rfbsPartialCompensationReturn) {
        this.rfbsPartialCompensationReturn = rfbsPartialCompensationReturn;
    }

    public BigDecimal getServicesTotal() {
        return servicesTotal;
    }

    public void setServicesTotal(BigDecimal servicesTotal) {
        this.servicesTotal = servicesTotal;
    }

    public BigDecimal getOthersTotal() {
        return othersTotal;
    }

    public void setOthersTotal(BigDecimal othersTotal) {
        this.othersTotal = othersTotal;
    }

    public BigDecimal getEndBalanceAmount() {
        return endBalanceAmount;
    }

    public void setEndBalanceAmount(BigDecimal endBalanceAmount) {
        this.endBalanceAmount = endBalanceAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
