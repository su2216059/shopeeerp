package com.example.shopeeerp.adapter.dto.ozon;

import java.util.List;

/**
 * Ozon商品详情请求DTO
 */
public class OzonProductInfoRequest {
    private List<String> offer_id;
    private List<String> product_id;
    private List<String> sku;
    private Integer limit;
    private String last_id;
    private String sort_dir;



    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getLast_id() {
        return last_id;
    }

    public void setLast_id(String last_id) {
        this.last_id = last_id;
    }

    public String getSort_dir() {
        return sort_dir;
    }

    public void setSort_dir(String sort_dir) {
        this.sort_dir = sort_dir;
    }

    public List<String> getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(List<String> offer_id) {
        this.offer_id = offer_id;
    }

    public List<String> getProduct_id() {
        return product_id;
    }

    public void setProduct_id(List<String> product_id) {
        this.product_id = product_id;
    }

    public List<String> getSku() {
        return sku;
    }

    public void setSku(List<String> sku) {
        this.sku = sku;
    }
}
