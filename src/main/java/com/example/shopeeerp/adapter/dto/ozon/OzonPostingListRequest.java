package com.example.shopeeerp.adapter.dto.ozon;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 请求体：/v3/posting/fbs/list
 */
public class OzonPostingListRequest {

    private String dir;
    private Filter filter;
    private Integer limit;
    private Integer offset;
    @JsonProperty("with")
    private With withFields;

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public With getWithFields() {
        return withFields;
    }

    public void setWithFields(With withFields) {
        this.withFields = withFields;
    }

    public static class Filter {
        @JsonProperty("delivery_method_id")
        private List<Long> deliveryMethodId;
        @JsonProperty("fbpFilter")
        private String fbpFilter;
        @JsonProperty("last_changed_status_date")
        private LastChangedStatusDate lastChangedStatusDate;
        @JsonProperty("order_id")
        private Long orderId;
        @JsonProperty("provider_id")
        private List<Long> providerId;
        private String since;
        private String status;
        private String to;
        @JsonProperty("warehouse_id")
        private List<Long> warehouseId;

        public List<Long> getDeliveryMethodId() {
            return deliveryMethodId;
        }

        public void setDeliveryMethodId(List<Long> deliveryMethodId) {
            this.deliveryMethodId = deliveryMethodId;
        }

        public String getFbpFilter() {
            return fbpFilter;
        }

        public void setFbpFilter(String fbpFilter) {
            this.fbpFilter = fbpFilter;
        }

        public LastChangedStatusDate getLastChangedStatusDate() {
            return lastChangedStatusDate;
        }

        public void setLastChangedStatusDate(LastChangedStatusDate lastChangedStatusDate) {
            this.lastChangedStatusDate = lastChangedStatusDate;
        }

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public List<Long> getProviderId() {
            return providerId;
        }

        public void setProviderId(List<Long> providerId) {
            this.providerId = providerId;
        }

        public String getSince() {
            return since;
        }

        public void setSince(String since) {
            this.since = since;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public List<Long> getWarehouseId() {
            return warehouseId;
        }

        public void setWarehouseId(List<Long> warehouseId) {
            this.warehouseId = warehouseId;
        }
    }

    public static class LastChangedStatusDate {
        private String from;
        private String to;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }
    }

    public static class With {
        @JsonProperty("analytics_data")
        private Boolean analyticsData;
        private Boolean barcodes;
        @JsonProperty("financial_data")
        private Boolean financialData;
        private Boolean translit;

        public Boolean getAnalyticsData() {
            return analyticsData;
        }

        public void setAnalyticsData(Boolean analyticsData) {
            this.analyticsData = analyticsData;
        }

        public Boolean getBarcodes() {
            return barcodes;
        }

        public void setBarcodes(Boolean barcodes) {
            this.barcodes = barcodes;
        }

        public Boolean getFinancialData() {
            return financialData;
        }

        public void setFinancialData(Boolean financialData) {
            this.financialData = financialData;
        }

        public Boolean getTranslit() {
            return translit;
        }

        public void setTranslit(Boolean translit) {
            this.translit = translit;
        }
    }
}
