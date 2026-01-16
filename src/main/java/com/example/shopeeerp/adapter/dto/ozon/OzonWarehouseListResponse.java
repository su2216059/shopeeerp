package com.example.shopeeerp.adapter.dto.ozon;

import java.util.List;

public class OzonWarehouseListResponse {
    private List<Warehouse> result;

    public List<Warehouse> getResult() {
        return result;
    }

    public void setResult(List<Warehouse> result) {
        this.result = result;
    }

    public static class Warehouse {
        private Boolean has_entrusted_acceptance;
        private Boolean is_rfbs;
        private String name;
        private Long warehouse_id;
        private Boolean can_print_act_in_advance;
        private FirstMileType first_mile_type;
        private Boolean has_postings_limit;
        private Boolean is_karantin;
        private Boolean is_kgt;
        private Boolean is_timetable_editable;
        private Integer min_postings_limit;
        private Integer postings_limit;
        private Integer min_working_days;
        private String status;
        private List<String> working_days;

        public Boolean getHas_entrusted_acceptance() {
            return has_entrusted_acceptance;
        }

        public void setHas_entrusted_acceptance(Boolean has_entrusted_acceptance) {
            this.has_entrusted_acceptance = has_entrusted_acceptance;
        }

        public Boolean getIs_rfbs() {
            return is_rfbs;
        }

        public void setIs_rfbs(Boolean is_rfbs) {
            this.is_rfbs = is_rfbs;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getWarehouse_id() {
            return warehouse_id;
        }

        public void setWarehouse_id(Long warehouse_id) {
            this.warehouse_id = warehouse_id;
        }

        public Boolean getCan_print_act_in_advance() {
            return can_print_act_in_advance;
        }

        public void setCan_print_act_in_advance(Boolean can_print_act_in_advance) {
            this.can_print_act_in_advance = can_print_act_in_advance;
        }

        public FirstMileType getFirst_mile_type() {
            return first_mile_type;
        }

        public void setFirst_mile_type(FirstMileType first_mile_type) {
            this.first_mile_type = first_mile_type;
        }

        public Boolean getHas_postings_limit() {
            return has_postings_limit;
        }

        public void setHas_postings_limit(Boolean has_postings_limit) {
            this.has_postings_limit = has_postings_limit;
        }

        public Boolean getIs_karantin() {
            return is_karantin;
        }

        public void setIs_karantin(Boolean is_karantin) {
            this.is_karantin = is_karantin;
        }

        public Boolean getIs_kgt() {
            return is_kgt;
        }

        public void setIs_kgt(Boolean is_kgt) {
            this.is_kgt = is_kgt;
        }

        public Boolean getIs_timetable_editable() {
            return is_timetable_editable;
        }

        public void setIs_timetable_editable(Boolean is_timetable_editable) {
            this.is_timetable_editable = is_timetable_editable;
        }

        public Integer getMin_postings_limit() {
            return min_postings_limit;
        }

        public void setMin_postings_limit(Integer min_postings_limit) {
            this.min_postings_limit = min_postings_limit;
        }

        public Integer getPostings_limit() {
            return postings_limit;
        }

        public void setPostings_limit(Integer postings_limit) {
            this.postings_limit = postings_limit;
        }

        public Integer getMin_working_days() {
            return min_working_days;
        }

        public void setMin_working_days(Integer min_working_days) {
            this.min_working_days = min_working_days;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<String> getWorking_days() {
            return working_days;
        }

        public void setWorking_days(List<String> working_days) {
            this.working_days = working_days;
        }
    }

    public static class FirstMileType {
        private String dropoff_point_id;
        private Long dropoff_timeslot_id;
        private Boolean first_mile_is_changing;
        private String first_mile_type;

        public String getDropoff_point_id() {
            return dropoff_point_id;
        }

        public void setDropoff_point_id(String dropoff_point_id) {
            this.dropoff_point_id = dropoff_point_id;
        }

        public Long getDropoff_timeslot_id() {
            return dropoff_timeslot_id;
        }

        public void setDropoff_timeslot_id(Long dropoff_timeslot_id) {
            this.dropoff_timeslot_id = dropoff_timeslot_id;
        }

        public Boolean getFirst_mile_is_changing() {
            return first_mile_is_changing;
        }

        public void setFirst_mile_is_changing(Boolean first_mile_is_changing) {
            this.first_mile_is_changing = first_mile_is_changing;
        }

        public String getFirst_mile_type() {
            return first_mile_type;
        }

        public void setFirst_mile_type(String first_mile_type) {
            this.first_mile_type = first_mile_type;
        }
    }
}