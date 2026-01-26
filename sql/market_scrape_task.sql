-- Market scrape task queue and scheduler status

DROP TABLE IF EXISTS market_scrape_task;
DROP TABLE IF EXISTS market_scrape_scheduler;

CREATE TABLE market_scrape_task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  platform VARCHAR(20) COMMENT 'platform name',
  market VARCHAR(10) COMMENT 'market code',
  url VARCHAR(1000) NOT NULL COMMENT 'target url',
  data_type VARCHAR(64) COMMENT 'detail_page / search_page / category_page',
  status VARCHAR(32) NOT NULL COMMENT 'PENDING / IN_PROGRESS / RETRY / DONE / FAILED',
  priority INT DEFAULT 0 COMMENT 'higher value is higher priority',
  scheduled_at DATETIME COMMENT 'when task should be picked',
  fetched_at DATETIME COMMENT 'when task finished',
  retry_count INT DEFAULT 0 COMMENT 'retry count',
  max_retries INT DEFAULT 3 COMMENT 'max retries',
  last_error VARCHAR(255) COMMENT 'last error',
  payload_json LONGTEXT COMMENT 'extra payload',
  lock_owner VARCHAR(64) COMMENT 'worker id',
  lock_at DATETIME COMMENT 'lock time',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_status_time (status, scheduled_at),
  KEY idx_lock_owner (lock_owner)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='market scrape task';

CREATE TABLE market_scrape_scheduler (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  worker_id VARCHAR(64) NOT NULL COMMENT 'scheduler worker id',
  status VARCHAR(32) COMMENT 'RUNNING / IDLE / STOPPED',
  current_concurrency INT DEFAULT 0,
  max_concurrency INT DEFAULT 0,
  last_heartbeat DATETIME,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_worker_id (worker_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='market scrape scheduler';

-- Worker注册和心跳表
CREATE TABLE IF NOT EXISTS market_scrape_worker (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  worker_id VARCHAR(100) UNIQUE NOT NULL COMMENT 'Worker唯一ID',
  worker_name VARCHAR(100) COMMENT 'Worker名称',
  browser_type VARCHAR(50) COMMENT '浏览器类型',
  browser_version VARCHAR(50) COMMENT '浏览器版本',
  script_version VARCHAR(20) DEFAULT '2.7' COMMENT '脚本版本',
  status VARCHAR(20) NOT NULL DEFAULT 'idle' COMMENT 'idle, busy, offline',
  current_task_id BIGINT COMMENT '当前任务ID',
  total_tasks INT DEFAULT 0 COMMENT '总任务数',
  success_tasks INT DEFAULT 0 COMMENT '成功任务数',
  failed_tasks INT DEFAULT 0 COMMENT '失败任务数',
  last_heartbeat DATETIME COMMENT '最后心跳时间',
  last_ip VARCHAR(50) COMMENT '最后IP地址',
  registered_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_status (status),
  INDEX idx_heartbeat (last_heartbeat)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抓取Worker注册表';
