package com.example.shopeeerp.adapter.dto.ozon;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OzonCashflowRequest {

    @JsonProperty("date")
    private DateRange date;
    @JsonProperty("with_details")
    private Boolean withDetails;
    private Integer page;
    @JsonProperty("page_size")
    private Integer pageSize;

    public DateRange getDate() {
        return date;
    }

    public void setDate(DateRange date) {
        this.date = date;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Boolean getWithDetails() {
        return withDetails;
    }

    public void setWithDetails(Boolean withDetails) {
        this.withDetails = withDetails;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public static class DateRange {
        @JsonProperty("from")
        private String from;
        @JsonProperty("to")
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
}
