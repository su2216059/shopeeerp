package com.example.shopeeerp.controller;

import com.example.shopeeerp.pojo.MarketScrapeScheduler;
import com.example.shopeeerp.service.MarketScrapeSchedulerService;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/market/schedulers")
@CrossOrigin(origins = "*")
public class MarketScrapeSchedulerController {

    @Autowired
    private MarketScrapeSchedulerService schedulerService;

    @PostMapping("/heartbeat")
    public ResponseEntity<Map<String, Object>> heartbeat(@RequestBody(required = false) HeartbeatRequest request) {
        Map<String, Object> resp = new HashMap<>();
        if (request == null || request.getWorkerId() == null || request.getWorkerId().trim().isEmpty()) {
            resp.put("success", false);
            resp.put("message", "workerId is required");
            return ResponseEntity.badRequest().body(resp);
        }
        MarketScrapeScheduler scheduler = new MarketScrapeScheduler();
        scheduler.setWorkerId(request.getWorkerId());
        scheduler.setStatus(request.getStatus());
        scheduler.setCurrentConcurrency(request.getCurrentConcurrency());
        scheduler.setMaxConcurrency(request.getMaxConcurrency());
        scheduler.setLastHeartbeat(parseDateTime(request.getLastHeartbeat()));
        boolean ok = schedulerService.heartbeat(scheduler);
        resp.put("success", ok);
        return ResponseEntity.ok(resp);
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value.trim()).toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }

    public static class HeartbeatRequest {
        @JsonProperty("worker_id")
        @JsonAlias("workerId")
        private String workerId;
        private String status;
        @JsonProperty("current_concurrency")
        @JsonAlias("currentConcurrency")
        private Integer currentConcurrency;
        @JsonProperty("max_concurrency")
        @JsonAlias("maxConcurrency")
        private Integer maxConcurrency;
        @JsonProperty("last_heartbeat")
        @JsonAlias("lastHeartbeat")
        private String lastHeartbeat;

        public String getWorkerId() {
            return workerId;
        }

        public void setWorkerId(String workerId) {
            this.workerId = workerId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getCurrentConcurrency() {
            return currentConcurrency;
        }

        public void setCurrentConcurrency(Integer currentConcurrency) {
            this.currentConcurrency = currentConcurrency;
        }

        public Integer getMaxConcurrency() {
            return maxConcurrency;
        }

        public void setMaxConcurrency(Integer maxConcurrency) {
            this.maxConcurrency = maxConcurrency;
        }

        public String getLastHeartbeat() {
            return lastHeartbeat;
        }

        public void setLastHeartbeat(String lastHeartbeat) {
            this.lastHeartbeat = lastHeartbeat;
        }
    }
}
