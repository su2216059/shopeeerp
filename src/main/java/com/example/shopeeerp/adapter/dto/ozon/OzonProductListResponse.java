package com.example.shopeeerp.adapter.dto.ozon;

import java.util.List;

/**
 * Ozon商品列表响应DTO
 */
public class OzonProductListResponse {
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Result {
        private List<Item> items;
        private Integer total;
        private String last_id;

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public String getLast_id() {
            return last_id;
        }

        public void setLast_id(String last_id) {
            this.last_id = last_id;
        }
    }

    public static class Item {
        private Boolean archived;
        private Boolean has_fbo_stocks;
        private Boolean has_fbs_stocks;
        private Boolean is_discounted;
        private String offer_id;
        private Long product_id;
        private List<Quant> quants;

        public Boolean getArchived() {
            return archived;
        }

        public void setArchived(Boolean archived) {
            this.archived = archived;
        }

        public Boolean getHas_fbo_stocks() {
            return has_fbo_stocks;
        }

        public void setHas_fbo_stocks(Boolean has_fbo_stocks) {
            this.has_fbo_stocks = has_fbo_stocks;
        }

        public Boolean getHas_fbs_stocks() {
            return has_fbs_stocks;
        }

        public void setHas_fbs_stocks(Boolean has_fbs_stocks) {
            this.has_fbs_stocks = has_fbs_stocks;
        }

        public Boolean getIs_discounted() {
            return is_discounted;
        }

        public void setIs_discounted(Boolean is_discounted) {
            this.is_discounted = is_discounted;
        }

        public String getOffer_id() {
            return offer_id;
        }

        public void setOffer_id(String offer_id) {
            this.offer_id = offer_id;
        }

        public Long getProduct_id() {
            return product_id;
        }

        public void setProduct_id(Long product_id) {
            this.product_id = product_id;
        }

        public List<Quant> getQuants() {
            return quants;
        }

        public void setQuants(List<Quant> quants) {
            this.quants = quants;
        }
    }

    public static class Quant {
        private String quant_code;
        private Integer quant_size;

        public String getQuant_code() {
            return quant_code;
        }

        public void setQuant_code(String quant_code) {
            this.quant_code = quant_code;
        }

        public Integer getQuant_size() {
            return quant_size;
        }

        public void setQuant_size(Integer quant_size) {
            this.quant_size = quant_size;
        }
    }
}
