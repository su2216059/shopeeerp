package com.example.shopeeerp.adapter.dto.ozon;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 响应体：/v3/posting/fbs/list
 */
public class OzonPostingListResponse {

    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Result {
        private List<Posting> postings;
        @JsonProperty("has_next")
        private Boolean hasNext;
        private Integer count;

        public List<Posting> getPostings() {
            return postings;
        }

        public void setPostings(List<Posting> postings) {
            this.postings = postings;
        }

        public Boolean getHasNext() {
            return hasNext;
        }

        public void setHasNext(Boolean hasNext) {
            this.hasNext = hasNext;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }

    public static class Posting {
        @JsonProperty("posting_number")
        private String postingNumber;
        @JsonProperty("order_id")
        private Long orderId;
        @JsonProperty("order_number")
        private String orderNumber;
        private String status;
        private String substatus;
        @JsonProperty("delivery_method")
        private DeliveryMethod deliveryMethod;
        @JsonProperty("tracking_number")
        private String trackingNumber;
        @JsonProperty("tpl_integration_type")
        private String tplIntegrationType;
        @JsonProperty("in_process_at")
        private String inProcessAt;
        @JsonProperty("shipment_date")
        private String shipmentDate;
        @JsonProperty("shipment_date_without_delay")
        private String shipmentDateWithoutDelay;
        @JsonProperty("delivering_date")
        private String deliveringDate;
        @JsonProperty("is_express")
        private Boolean isExpress;
        private Object optional;
        private Object cancellation;
        @JsonProperty("analytics_data")
        private Object analyticsData;
        @JsonProperty("financial_data")
        private Object financialData;
        private Object requirements;
        private Tariffication tariffication;
        @JsonProperty("available_actions")
        private List<String> availableActions;
        private Object barcodes;
        private Object customer;
        private Object addressee;
        private List<Product> products;

        public String getPostingNumber() {
            return postingNumber;
        }

        public void setPostingNumber(String postingNumber) {
            this.postingNumber = postingNumber;
        }

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSubstatus() {
            return substatus;
        }

        public void setSubstatus(String substatus) {
            this.substatus = substatus;
        }

        public DeliveryMethod getDeliveryMethod() {
            return deliveryMethod;
        }

        public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
            this.deliveryMethod = deliveryMethod;
        }

        public String getTrackingNumber() {
            return trackingNumber;
        }

        public void setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }

        public String getTplIntegrationType() {
            return tplIntegrationType;
        }

        public void setTplIntegrationType(String tplIntegrationType) {
            this.tplIntegrationType = tplIntegrationType;
        }

        public String getInProcessAt() {
            return inProcessAt;
        }

        public void setInProcessAt(String inProcessAt) {
            this.inProcessAt = inProcessAt;
        }

        public String getShipmentDate() {
            return shipmentDate;
        }

        public void setShipmentDate(String shipmentDate) {
            this.shipmentDate = shipmentDate;
        }

        public String getShipmentDateWithoutDelay() {
            return shipmentDateWithoutDelay;
        }

        public void setShipmentDateWithoutDelay(String shipmentDateWithoutDelay) {
            this.shipmentDateWithoutDelay = shipmentDateWithoutDelay;
        }

        public String getDeliveringDate() {
            return deliveringDate;
        }

        public void setDeliveringDate(String deliveringDate) {
            this.deliveringDate = deliveringDate;
        }

        public Boolean getIsExpress() {
            return isExpress;
        }

        public void setIsExpress(Boolean isExpress) {
            this.isExpress = isExpress;
        }

        public Object getOptional() {
            return optional;
        }

        public void setOptional(Object optional) {
            this.optional = optional;
        }

        public Object getCancellation() {
            return cancellation;
        }

        public void setCancellation(Object cancellation) {
            this.cancellation = cancellation;
        }

        public Object getAnalyticsData() {
            return analyticsData;
        }

        public void setAnalyticsData(Object analyticsData) {
            this.analyticsData = analyticsData;
        }

        public Object getFinancialData() {
            return financialData;
        }

        public void setFinancialData(Object financialData) {
            this.financialData = financialData;
        }

        public Object getRequirements() {
            return requirements;
        }

        public void setRequirements(Object requirements) {
            this.requirements = requirements;
        }

        public Tariffication getTariffication() {
            return tariffication;
        }

        public void setTariffication(Tariffication tariffication) {
            this.tariffication = tariffication;
        }

        public List<String> getAvailableActions() {
            return availableActions;
        }

        public void setAvailableActions(List<String> availableActions) {
            this.availableActions = availableActions;
        }

        public Object getBarcodes() {
            return barcodes;
        }

        public void setBarcodes(Object barcodes) {
            this.barcodes = barcodes;
        }

        public Object getCustomer() {
            return customer;
        }

        public void setCustomer(Object customer) {
            this.customer = customer;
        }

        public Object getAddressee() {
            return addressee;
        }

        public void setAddressee(Object addressee) {
            this.addressee = addressee;
        }

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }
    }

    public static class DeliveryMethod {
        private Long id;
        private String name;
        @JsonProperty("warehouse_id")
        private Long warehouseId;
        private String warehouse;
        @JsonProperty("tpl_provider_id")
        private Long tplProviderId;
        @JsonProperty("tpl_provider")
        private String tplProvider;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getWarehouseId() {
            return warehouseId;
        }

        public void setWarehouseId(Long warehouseId) {
            this.warehouseId = warehouseId;
        }

        public String getWarehouse() {
            return warehouse;
        }

        public void setWarehouse(String warehouse) {
            this.warehouse = warehouse;
        }

        public Long getTplProviderId() {
            return tplProviderId;
        }

        public void setTplProviderId(Long tplProviderId) {
            this.tplProviderId = tplProviderId;
        }

        public String getTplProvider() {
            return tplProvider;
        }

        public void setTplProvider(String tplProvider) {
            this.tplProvider = tplProvider;
        }
    }

    public static class Product {
        private String price;
        @JsonProperty("currency_code")
        private String currencyCode;
        @JsonProperty("offer_id")
        private String offerId;
        private String name;
        private Long sku;
        private Integer quantity;
        private List<String> imei;

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
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

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public List<String> getImei() {
            return imei;
        }

        public void setImei(List<String> imei) {
            this.imei = imei;
        }
    }

    public static class Tariffication {
        @JsonProperty("current_tariff_rate")
        private Integer currentTariffRate;
        @JsonProperty("current_tariff_type")
        private String currentTariffType;
        @JsonProperty("current_tariff_charge")
        private String currentTariffCharge;
        @JsonProperty("current_tariff_charge_currency_code")
        private String currentTariffChargeCurrencyCode;
        @JsonProperty("next_tariff_rate")
        private Integer nextTariffRate;
        @JsonProperty("next_tariff_type")
        private String nextTariffType;
        @JsonProperty("next_tariff_charge")
        private String nextTariffCharge;
        @JsonProperty("next_tariff_starts_at")
        private String nextTariffStartsAt;
        @JsonProperty("next_tariff_charge_currency_code")
        private String nextTariffChargeCurrencyCode;

        public Integer getCurrentTariffRate() {
            return currentTariffRate;
        }

        public void setCurrentTariffRate(Integer currentTariffRate) {
            this.currentTariffRate = currentTariffRate;
        }

        public String getCurrentTariffType() {
            return currentTariffType;
        }

        public void setCurrentTariffType(String currentTariffType) {
            this.currentTariffType = currentTariffType;
        }

        public String getCurrentTariffCharge() {
            return currentTariffCharge;
        }

        public void setCurrentTariffCharge(String currentTariffCharge) {
            this.currentTariffCharge = currentTariffCharge;
        }

        public String getCurrentTariffChargeCurrencyCode() {
            return currentTariffChargeCurrencyCode;
        }

        public void setCurrentTariffChargeCurrencyCode(String currentTariffChargeCurrencyCode) {
            this.currentTariffChargeCurrencyCode = currentTariffChargeCurrencyCode;
        }

        public Integer getNextTariffRate() {
            return nextTariffRate;
        }

        public void setNextTariffRate(Integer nextTariffRate) {
            this.nextTariffRate = nextTariffRate;
        }

        public String getNextTariffType() {
            return nextTariffType;
        }

        public void setNextTariffType(String nextTariffType) {
            this.nextTariffType = nextTariffType;
        }

        public String getNextTariffCharge() {
            return nextTariffCharge;
        }

        public void setNextTariffCharge(String nextTariffCharge) {
            this.nextTariffCharge = nextTariffCharge;
        }

        public String getNextTariffStartsAt() {
            return nextTariffStartsAt;
        }

        public void setNextTariffStartsAt(String nextTariffStartsAt) {
            this.nextTariffStartsAt = nextTariffStartsAt;
        }

        public String getNextTariffChargeCurrencyCode() {
            return nextTariffChargeCurrencyCode;
        }

        public void setNextTariffChargeCurrencyCode(String nextTariffChargeCurrencyCode) {
            this.nextTariffChargeCurrencyCode = nextTariffChargeCurrencyCode;
        }
    }
}
