package com.example.shopeeerp.adapter.dto.ozon;

import java.util.List;

/**
 * Ozon商品列表请求DTO
 */
public class OzonProductListRequest {
    private Filter filter;
    private String last_id;
    private Integer limit;

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public String getLast_id() {
        return last_id;
    }

    public void setLast_id(String last_id) {
        this.last_id = last_id;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public static class Filter {
        private List<String> offer_id;
        private List<Long> product_id;
        private String visibility;

        public List<String> getOffer_id() {
            return offer_id;
        }

        public void setOffer_id(List<String> offer_id) {
            this.offer_id = offer_id;
        }

        public List<Long> getProduct_id() {
            return product_id;
        }

        public void setProduct_id(List<Long> product_id) {
            this.product_id = product_id;
        }

        public String getVisibility() {
            return visibility;
        }

        public void setVisibility(String visibility) {
            this.visibility = visibility;
        }
    }
}
