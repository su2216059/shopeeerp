package com.example.shopeeerp.adapter.dto.ozon;

import java.util.List;

public class OzonDeliveryMethodListResponse {
    private List<DeliveryMethod> result;
    private Boolean has_next;

    public List<DeliveryMethod> getResult() {
        return result;
    }

    public void setResult(List<DeliveryMethod> result) {
        this.result = result;
    }

    public Boolean getHas_next() {
        return has_next;
    }

    public void setHas_next(Boolean has_next) {
        this.has_next = has_next;
    }

    public static class DeliveryMethod {
        private Long id;
        private Long company_id;
        private String name;
        private String status;
        private String cutoff;
        private Long provider_id;
        private Long template_id;
        private Long warehouse_id;
        private String created_at;
        private String updated_at;
        private Long sla_cut_in;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getCompany_id() {
            return company_id;
        }

        public void setCompany_id(Long company_id) {
            this.company_id = company_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCutoff() {
            return cutoff;
        }

        public void setCutoff(String cutoff) {
            this.cutoff = cutoff;
        }

        public Long getProvider_id() {
            return provider_id;
        }

        public void setProvider_id(Long provider_id) {
            this.provider_id = provider_id;
        }

        public Long getTemplate_id() {
            return template_id;
        }

        public void setTemplate_id(Long template_id) {
            this.template_id = template_id;
        }

        public Long getWarehouse_id() {
            return warehouse_id;
        }

        public void setWarehouse_id(Long warehouse_id) {
            this.warehouse_id = warehouse_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public Long getSla_cut_in() {
            return sla_cut_in;
        }

        public void setSla_cut_in(Long sla_cut_in) {
            this.sla_cut_in = sla_cut_in;
        }
    }
}