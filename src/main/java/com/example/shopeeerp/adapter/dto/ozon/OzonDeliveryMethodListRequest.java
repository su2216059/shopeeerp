package com.example.shopeeerp.adapter.dto.ozon;

public class OzonDeliveryMethodListRequest {
    private Filter filter;
    private Integer limit;
    private Integer offset;

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

    public static class Filter {
        private Long warehouse_id;
        private Long provider_id;
        private String status;

        public Long getWarehouse_id() {
            return warehouse_id;
        }

        public void setWarehouse_id(Long warehouse_id) {
            this.warehouse_id = warehouse_id;
        }

        public Long getProvider_id() {
            return provider_id;
        }

        public void setProvider_id(Long provider_id) {
            this.provider_id = provider_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}