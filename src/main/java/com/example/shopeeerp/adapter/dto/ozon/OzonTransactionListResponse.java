package com.example.shopeeerp.adapter.dto.ozon;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class OzonTransactionListResponse {

    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Result {
        private List<Operation> operations;
        @JsonProperty("page_count")
        private Integer pageCount;
        @JsonProperty("row_count")
        private Integer rowCount;

        public List<Operation> getOperations() {
            return operations;
        }

        public void setOperations(List<Operation> operations) {
            this.operations = operations;
        }

        public Integer getPageCount() {
            return pageCount;
        }

        public void setPageCount(Integer pageCount) {
            this.pageCount = pageCount;
        }

        public Integer getRowCount() {
            return rowCount;
        }

        public void setRowCount(Integer rowCount) {
            this.rowCount = rowCount;
        }
    }

    public static class Operation {
        @JsonProperty("operation_id")
        private Long operationId;
        @JsonProperty("operation_type")
        private String operationType;
        @JsonProperty("operation_type_name")
        private String operationTypeName;
        @JsonProperty("operation_date")
        private String operationDate;
        @JsonProperty("delivery_charge")
        private BigDecimal deliveryCharge;
        @JsonProperty("return_delivery_charge")
        private BigDecimal returnDeliveryCharge;
        @JsonProperty("accruals_for_sale")
        private BigDecimal accrualsForSale;
        @JsonProperty("sale_commission")
        private BigDecimal saleCommission;
        private BigDecimal amount;
        private String type;
        private Posting posting;
        private List<Map<String, Object>> items;
        private List<Map<String, Object>> services;

        public Long getOperationId() {
            return operationId;
        }

        public void setOperationId(Long operationId) {
            this.operationId = operationId;
        }

        public String getOperationType() {
            return operationType;
        }

        public void setOperationType(String operationType) {
            this.operationType = operationType;
        }

        public String getOperationTypeName() {
            return operationTypeName;
        }

        public void setOperationTypeName(String operationTypeName) {
            this.operationTypeName = operationTypeName;
        }

        public String getOperationDate() {
            return operationDate;
        }

        public void setOperationDate(String operationDate) {
            this.operationDate = operationDate;
        }

        public BigDecimal getDeliveryCharge() {
            return deliveryCharge;
        }

        public void setDeliveryCharge(BigDecimal deliveryCharge) {
            this.deliveryCharge = deliveryCharge;
        }

        public BigDecimal getReturnDeliveryCharge() {
            return returnDeliveryCharge;
        }

        public void setReturnDeliveryCharge(BigDecimal returnDeliveryCharge) {
            this.returnDeliveryCharge = returnDeliveryCharge;
        }

        public BigDecimal getAccrualsForSale() {
            return accrualsForSale;
        }

        public void setAccrualsForSale(BigDecimal accrualsForSale) {
            this.accrualsForSale = accrualsForSale;
        }

        public BigDecimal getSaleCommission() {
            return saleCommission;
        }

        public void setSaleCommission(BigDecimal saleCommission) {
            this.saleCommission = saleCommission;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Posting getPosting() {
            return posting;
        }

        public void setPosting(Posting posting) {
            this.posting = posting;
        }

        public List<Map<String, Object>> getItems() {
            return items;
        }

        public void setItems(List<Map<String, Object>> items) {
            this.items = items;
        }

        public List<Map<String, Object>> getServices() {
            return services;
        }

        public void setServices(List<Map<String, Object>> services) {
            this.services = services;
        }
    }

    public static class Posting {
        @JsonProperty("delivery_schema")
        private String deliverySchema;
        @JsonProperty("order_date")
        private String orderDate;
        @JsonProperty("posting_number")
        private String postingNumber;
        @JsonProperty("warehouse_id")
        private Long warehouseId;

        public String getDeliverySchema() {
            return deliverySchema;
        }

        public void setDeliverySchema(String deliverySchema) {
            this.deliverySchema = deliverySchema;
        }

        public String getOrderDate() {
            return orderDate;
        }

        public void setOrderDate(String orderDate) {
            this.orderDate = orderDate;
        }

        public String getPostingNumber() {
            return postingNumber;
        }

        public void setPostingNumber(String postingNumber) {
            this.postingNumber = postingNumber;
        }

        public Long getWarehouseId() {
            return warehouseId;
        }

        public void setWarehouseId(Long warehouseId) {
            this.warehouseId = warehouseId;
        }
    }
}
