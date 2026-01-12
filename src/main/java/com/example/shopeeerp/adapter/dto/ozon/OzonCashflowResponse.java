package com.example.shopeeerp.adapter.dto.ozon;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class OzonCashflowResponse {
    private Result result;
    @JsonProperty("page_count")
    private Integer pageCount;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public static class Result {
        @JsonProperty("cash_flows")
        private List<CashFlow> cashFlows;
        private List<Details> details;

        public List<CashFlow> getCashFlows() {
            return cashFlows;
        }

        public void setCashFlows(List<CashFlow> cashFlows) {
            this.cashFlows = cashFlows;
        }

        public List<Details> getDetails() {
            return details;
        }

        public void setDetails(List<Details> details) {
            this.details = details;
        }
    }

    public static class CashFlow {
        @JsonProperty("commission_amount")
        private BigDecimal commissionAmount;
        @JsonProperty("currency_code")
        private String currencyCode;
        @JsonProperty("item_delivery_and_return_amount")
        private BigDecimal itemDeliveryAndReturnAmount;
        @JsonProperty("orders_amount")
        private BigDecimal ordersAmount;
        @JsonProperty("returns_amount")
        private BigDecimal returnsAmount;
        @JsonProperty("services_amount")
        private BigDecimal servicesAmount;
        private Period period;

        public BigDecimal getCommissionAmount() {
            return commissionAmount;
        }

        public void setCommissionAmount(BigDecimal commissionAmount) {
            this.commissionAmount = commissionAmount;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        public BigDecimal getItemDeliveryAndReturnAmount() {
            return itemDeliveryAndReturnAmount;
        }

        public void setItemDeliveryAndReturnAmount(BigDecimal itemDeliveryAndReturnAmount) {
            this.itemDeliveryAndReturnAmount = itemDeliveryAndReturnAmount;
        }

        public BigDecimal getOrdersAmount() {
            return ordersAmount;
        }

        public void setOrdersAmount(BigDecimal ordersAmount) {
            this.ordersAmount = ordersAmount;
        }

        public BigDecimal getReturnsAmount() {
            return returnsAmount;
        }

        public void setReturnsAmount(BigDecimal returnsAmount) {
            this.returnsAmount = returnsAmount;
        }

        public BigDecimal getServicesAmount() {
            return servicesAmount;
        }

        public void setServicesAmount(BigDecimal servicesAmount) {
            this.servicesAmount = servicesAmount;
        }

        public Period getPeriod() {
            return period;
        }

        public void setPeriod(Period period) {
            this.period = period;
        }
    }

    public static class Period {
        private Long id;
        private String begin;
        private String end;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getBegin() {
            return begin;
        }

        public void setBegin(String begin) {
            this.begin = begin;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }

    public static class Details {
        private Period period;
        private List<Payment> payments;
        @JsonProperty("begin_balance_amount")
        private BigDecimal beginBalanceAmount;
        private Delivery delivery;
        @JsonProperty("return")
        private ReturnBlock returnBlock;
        private BigDecimal loan;
        @JsonProperty("invoice_transfer")
        private BigDecimal invoiceTransfer;
        private Rfbs rfbs;
        private Services services;
        private Others others;
        @JsonProperty("end_balance_amount")
        private BigDecimal endBalanceAmount;

        public Period getPeriod() {
            return period;
        }

        public void setPeriod(Period period) {
            this.period = period;
        }

        public List<Payment> getPayments() {
            return payments;
        }

        public void setPayments(List<Payment> payments) {
            this.payments = payments;
        }

        public BigDecimal getBeginBalanceAmount() {
            return beginBalanceAmount;
        }

        public void setBeginBalanceAmount(BigDecimal beginBalanceAmount) {
            this.beginBalanceAmount = beginBalanceAmount;
        }

        public Delivery getDelivery() {
            return delivery;
        }

        public void setDelivery(Delivery delivery) {
            this.delivery = delivery;
        }

        public ReturnBlock getReturnBlock() {
            return returnBlock;
        }

        public void setReturnBlock(ReturnBlock returnBlock) {
            this.returnBlock = returnBlock;
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

        public Rfbs getRfbs() {
            return rfbs;
        }

        public void setRfbs(Rfbs rfbs) {
            this.rfbs = rfbs;
        }

        public Services getServices() {
            return services;
        }

        public void setServices(Services services) {
            this.services = services;
        }

        public Others getOthers() {
            return others;
        }

        public void setOthers(Others others) {
            this.others = others;
        }

        public BigDecimal getEndBalanceAmount() {
            return endBalanceAmount;
        }

        public void setEndBalanceAmount(BigDecimal endBalanceAmount) {
            this.endBalanceAmount = endBalanceAmount;
        }
    }

    public static class Payment {
        private BigDecimal payment;
        @JsonProperty("currency_code")
        private String currencyCode;

        public BigDecimal getPayment() {
            return payment;
        }

        public void setPayment(BigDecimal payment) {
            this.payment = payment;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }
    }

    public static class Delivery {
        private BigDecimal total;
        private BigDecimal amount;
        @JsonProperty("delivery_services")
        private ServiceBlock deliveryServices;

        public BigDecimal getTotal() {
            return total;
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public ServiceBlock getDeliveryServices() {
            return deliveryServices;
        }

        public void setDeliveryServices(ServiceBlock deliveryServices) {
            this.deliveryServices = deliveryServices;
        }
    }

    public static class ReturnBlock {
        private BigDecimal total;
        private BigDecimal amount;
        @JsonProperty("return_services")
        private ServiceBlock returnServices;

        public BigDecimal getTotal() {
            return total;
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public ServiceBlock getReturnServices() {
            return returnServices;
        }

        public void setReturnServices(ServiceBlock returnServices) {
            this.returnServices = returnServices;
        }
    }

    public static class Rfbs {
        private BigDecimal total;
        @JsonProperty("transfer_delivery")
        private BigDecimal transferDelivery;
        @JsonProperty("transfer_delivery_return")
        private BigDecimal transferDeliveryReturn;
        @JsonProperty("compensation_delivery_return")
        private BigDecimal compensationDeliveryReturn;
        @JsonProperty("partial_compensation")
        private BigDecimal partialCompensation;
        @JsonProperty("partial_compensation_return")
        private BigDecimal partialCompensationReturn;

        public BigDecimal getTotal() {
            return total;
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }

        public BigDecimal getTransferDelivery() {
            return transferDelivery;
        }

        public void setTransferDelivery(BigDecimal transferDelivery) {
            this.transferDelivery = transferDelivery;
        }

        public BigDecimal getTransferDeliveryReturn() {
            return transferDeliveryReturn;
        }

        public void setTransferDeliveryReturn(BigDecimal transferDeliveryReturn) {
            this.transferDeliveryReturn = transferDeliveryReturn;
        }

        public BigDecimal getCompensationDeliveryReturn() {
            return compensationDeliveryReturn;
        }

        public void setCompensationDeliveryReturn(BigDecimal compensationDeliveryReturn) {
            this.compensationDeliveryReturn = compensationDeliveryReturn;
        }

        public BigDecimal getPartialCompensation() {
            return partialCompensation;
        }

        public void setPartialCompensation(BigDecimal partialCompensation) {
            this.partialCompensation = partialCompensation;
        }

        public BigDecimal getPartialCompensationReturn() {
            return partialCompensationReturn;
        }

        public void setPartialCompensationReturn(BigDecimal partialCompensationReturn) {
            this.partialCompensationReturn = partialCompensationReturn;
        }
    }

    public static class Services {
        private BigDecimal total;
        private List<ServiceItem> items;

        public BigDecimal getTotal() {
            return total;
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }

        public List<ServiceItem> getItems() {
            return items;
        }

        public void setItems(List<ServiceItem> items) {
            this.items = items;
        }
    }

    public static class Others {
        private BigDecimal total;
        private List<ServiceItem> items;

        public BigDecimal getTotal() {
            return total;
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }

        public List<ServiceItem> getItems() {
            return items;
        }

        public void setItems(List<ServiceItem> items) {
            this.items = items;
        }
    }

    public static class ServiceBlock {
        private BigDecimal total;
        private List<ServiceItem> items;

        public BigDecimal getTotal() {
            return total;
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }

        public List<ServiceItem> getItems() {
            return items;
        }

        public void setItems(List<ServiceItem> items) {
            this.items = items;
        }
    }

    public static class ServiceItem {
        private String name;
        private BigDecimal price;

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
    }
}
