package com.example.shopeeerp.controller;

import com.example.shopeeerp.pojo.MarketScrapeTask;
import com.example.shopeeerp.service.MarketScrapeTaskService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/market/tasks")
@CrossOrigin(origins = "*")
public class MarketScrapeTaskController {

    @Autowired
    private MarketScrapeTaskService taskService;

    @PostMapping("/enqueue")
    public ResponseEntity<Map<String, Object>> enqueue(@RequestBody(required = false) List<TaskRequest> requests) {
        Map<String, Object> resp = new HashMap<>();
        if (requests == null || requests.isEmpty()) {
            resp.put("success", false);
            resp.put("message", "payload is required");
            return ResponseEntity.badRequest().body(resp);
        }
        List<MarketScrapeTask> tasks = new ArrayList<>();
        for (TaskRequest request : requests) {
            MarketScrapeTask task = request != null ? request.toTask() : null;
            if (task != null) {
                tasks.add(task);
            }
        }
        boolean saved = taskService.enqueue(tasks);
        resp.put("success", saved);
        resp.put("count", tasks.size());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/pull")
    public ResponseEntity<Map<String, Object>> pull(@RequestBody(required = false) TaskPullRequest request) {
        Map<String, Object> resp = new HashMap<>();
        if (request == null || request.getWorkerId() == null || request.getWorkerId().trim().isEmpty()) {
            resp.put("success", false);
            resp.put("message", "workerId is required");
            return ResponseEntity.badRequest().body(resp);
        }
        List<MarketScrapeTask> tasks = taskService.pull(request.getWorkerId(), request.getLimit());
        resp.put("success", true);
        resp.put("count", tasks.size());
        resp.put("tasks", tasks);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/report")
    public ResponseEntity<Map<String, Object>> report(@RequestBody(required = false) TaskReportRequest request) {
        Map<String, Object> resp = new HashMap<>();
        if (request == null || request.getTaskId() == null) {
            resp.put("success", false);
            resp.put("message", "taskId is required");
            return ResponseEntity.badRequest().body(resp);
        }
        boolean ok;
        if ("SUCCESS".equalsIgnoreCase(request.getStatus())) {
            ok = taskService.reportSuccess(request.getTaskId(), parseDateTime(request.getFetchedAt()));
        } else {
            ok = taskService.reportFailure(request.getTaskId(), request.getErrorMessage());
        }
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

    public static class TaskRequest {
        private String platform;
        private String market;
        private String url;
        @JsonProperty("data_type")
        @JsonAlias("dataType")
        private String dataType;
        private Integer priority;
        @JsonProperty("scheduled_at")
        @JsonAlias("scheduledAt")
        private String scheduledAt;
        @JsonProperty("payload_json")
        @JsonAlias("payloadJson")
        private String payloadJson;
        @JsonProperty("max_retries")
        @JsonAlias("maxRetries")
        private Integer maxRetries;

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getMarket() {
            return market;
        }

        public void setMarket(String market) {
            this.market = market;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public Integer getPriority() {
            return priority;
        }

        public void setPriority(Integer priority) {
            this.priority = priority;
        }

        public String getScheduledAt() {
            return scheduledAt;
        }

        public void setScheduledAt(String scheduledAt) {
            this.scheduledAt = scheduledAt;
        }

        public String getPayloadJson() {
            return payloadJson;
        }

        public void setPayloadJson(String payloadJson) {
            this.payloadJson = payloadJson;
        }

        public Integer getMaxRetries() {
            return maxRetries;
        }

        public void setMaxRetries(Integer maxRetries) {
            this.maxRetries = maxRetries;
        }

        public MarketScrapeTask toTask() {
            MarketScrapeTask task = new MarketScrapeTask();
            task.setPlatform(platform);
            task.setMarket(market);
            task.setUrl(url);
            task.setDataType(dataType);
            task.setPriority(priority);
            task.setScheduledAt(parseDateTimeSafe(scheduledAt));
            task.setPayloadJson(payloadJson);
            task.setMaxRetries(maxRetries);
            return task;
        }

        private LocalDateTime parseDateTimeSafe(String value) {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            try {
                return OffsetDateTime.parse(value.trim()).toLocalDateTime();
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static class TaskPullRequest {
        @JsonProperty("worker_id")
        @JsonAlias("workerId")
        private String workerId;
        private Integer limit;

        public String getWorkerId() {
            return workerId;
        }

        public void setWorkerId(String workerId) {
            this.workerId = workerId;
        }

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }
    }

    public static class TaskReportRequest {
        @JsonProperty("task_id")
        @JsonAlias("taskId")
        private Long taskId;
        private String status;
        @JsonProperty("error_message")
        @JsonAlias("errorMessage")
        private String errorMessage;
        @JsonProperty("fetched_at")
        @JsonAlias("fetchedAt")
        private String fetchedAt;

        public Long getTaskId() {
            return taskId;
        }

        public void setTaskId(Long taskId) {
            this.taskId = taskId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getFetchedAt() {
            return fetchedAt;
        }

        public void setFetchedAt(String fetchedAt) {
            this.fetchedAt = fetchedAt;
        }
    }
}
