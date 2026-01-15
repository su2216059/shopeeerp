package com.example.shopeeerp.adapter.dto.ozon;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OzonTransactionListRequest {

    private Filter filter;
    private Integer page;
    @JsonProperty("page_size")
    private Integer pageSize;

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public static class Filter {
        @JsonProperty("posting_number")
        private String postingNumber;
        private DateRange date;

        public String getPostingNumber() {
            return postingNumber;
        }

        public void setPostingNumber(String postingNumber) {
            this.postingNumber = postingNumber;
        }

        public DateRange getDate() {
            return date;
        }

        public void setDate(DateRange date) {
            this.date = date;
        }
    }

    public static class DateRange {
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
}
