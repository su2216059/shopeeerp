package com.example.shopeeerp.controller;

import com.example.shopeeerp.pojo.MarketScrapeWorker;
import com.example.shopeeerp.service.MarketScrapeWorkerService;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/market/workers")
@CrossOrigin(origins = "*")
public class MarketScrapeWorkerController {

    @Autowired
    private MarketScrapeWorkerService workerService;

    /**
     * Worker注册
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody(required = false) WorkerRegisterRequest request,
            HttpServletRequest httpRequest) {
        Map<String, Object> resp = new HashMap<>();

        if (request == null || request.getWorkerId() == null || request.getWorkerId().trim().isEmpty()) {
            resp.put("success", false);
            resp.put("message", "worker_id is required");
            return ResponseEntity.badRequest().body(resp);
        }

        MarketScrapeWorker worker = new MarketScrapeWorker();
        worker.setWorkerId(request.getWorkerId());
        worker.setWorkerName(request.getWorkerName());
        worker.setBrowserType(request.getBrowserType());
        worker.setBrowserVersion(request.getBrowserVersion());
        worker.setScriptVersion(request.getScriptVersion());
        worker.setLastIp(getClientIp(httpRequest));

        boolean success = workerService.registerWorker(worker);
        resp.put("success", success);
        resp.put("worker_id", request.getWorkerId());

        return ResponseEntity.ok(resp);
    }

    /**
     * Worker心跳
     */
    @PostMapping("/heartbeat")
    public ResponseEntity<Map<String, Object>> heartbeat(
            @RequestBody(required = false) WorkerHeartbeatRequest request) {
        Map<String, Object> resp = new HashMap<>();

        if (request == null || request.getWorkerId() == null || request.getWorkerId().trim().isEmpty()) {
            resp.put("success", false);
            resp.put("message", "worker_id is required");
            return ResponseEntity.badRequest().body(resp);
        }

        boolean success = workerService.heartbeat(request.getWorkerId());
        resp.put("success", success);

        return ResponseEntity.ok(resp);
    }

    /**
     * 查询在线Worker列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list() {
        Map<String, Object> resp = new HashMap<>();

        List<MarketScrapeWorker> workers = workerService.getOnlineWorkers();
        resp.put("success", true);
        resp.put("count", workers.size());
        resp.put("workers", workers);

        return ResponseEntity.ok(resp);
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    // ========== Request DTOs ==========

    public static class WorkerRegisterRequest {
        @JsonProperty("worker_id")
        @JsonAlias("workerId")
        private String workerId;

        @JsonProperty("worker_name")
        @JsonAlias("workerName")
        private String workerName;

        @JsonProperty("browser_type")
        @JsonAlias("browserType")
        private String browserType;

        @JsonProperty("browser_version")
        @JsonAlias("browserVersion")
        private String browserVersion;

        @JsonProperty("script_version")
        @JsonAlias("scriptVersion")
        private String scriptVersion;

        public String getWorkerId() {
            return workerId;
        }

        public void setWorkerId(String workerId) {
            this.workerId = workerId;
        }

        public String getWorkerName() {
            return workerName;
        }

        public void setWorkerName(String workerName) {
            this.workerName = workerName;
        }

        public String getBrowserType() {
            return browserType;
        }

        public void setBrowserType(String browserType) {
            this.browserType = browserType;
        }

        public String getBrowserVersion() {
            return browserVersion;
        }

        public void setBrowserVersion(String browserVersion) {
            this.browserVersion = browserVersion;
        }

        public String getScriptVersion() {
            return scriptVersion;
        }

        public void setScriptVersion(String scriptVersion) {
            this.scriptVersion = scriptVersion;
        }
    }

    public static class WorkerHeartbeatRequest {
        @JsonProperty("worker_id")
        @JsonAlias("workerId")
        private String workerId;

        public String getWorkerId() {
            return workerId;
        }

        public void setWorkerId(String workerId) {
            this.workerId = workerId;
        }
    }
}
