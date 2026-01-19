-- Market scrape raw records

DROP TABLE IF EXISTS market_scrape_record;

CREATE TABLE market_scrape_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  platform VARCHAR(20) COMMENT 'platform name',
  market VARCHAR(10) COMMENT 'market code',
  url VARCHAR(1000) COMMENT 'source url',
  data_type VARCHAR(64) COMMENT 'detail_page / search_page / category_page',
  fetched_at DATETIME COMMENT 'fetch time',
  raw_payload LONGTEXT COMMENT 'raw response',
  parsed_payload LONGTEXT COMMENT 'parsed json',
  status VARCHAR(32) COMMENT 'status',
  error_message VARCHAR(255) COMMENT 'error message',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_platform_time (platform, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='market scrape record';
