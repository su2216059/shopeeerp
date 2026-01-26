package com.example.shopeeerp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


import java.time.LocalDateTime;

@Data
public class MarketScrapeWorker {

    private Long id;

    @JsonProperty("worker_id")
    private String workerId;

    @JsonProperty("worker_name")
    private String workerName;

    @JsonProperty("browser_type")
    private String browserType;

    @JsonProperty("browser_version")
    private String browserVersion;

    @JsonProperty("script_version")
    private String scriptVersion;

    private String status;  // idle, busy, offline

    @JsonProperty("current_task_id")
    private Long currentTaskId;

    @JsonProperty("total_tasks")
    private Integer totalTasks;

    @JsonProperty("success_tasks")
    private Integer successTasks;

    @JsonProperty("failed_tasks")
    private Integer failedTasks;

    @JsonProperty("last_heartbeat")
    private LocalDateTime lastHeartbeat;

    @JsonProperty("last_ip")
    private String lastIp;

    @JsonProperty("registered_at")
    private LocalDateTime registeredAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
