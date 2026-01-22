-- Market signal core tables

DROP TABLE IF EXISTS market_product_snapshot;
DROP TABLE IF EXISTS market_product;

CREATE TABLE market_product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  platform VARCHAR(20) NOT NULL COMMENT 'platform name',
  market VARCHAR(10) DEFAULT 'RU' COMMENT 'market code',

  platform_product_id VARCHAR(64) NOT NULL COMMENT 'platform product id',
  platform_sku_id VARCHAR(64) COMMENT 'platform sku id',

  title VARCHAR(512),
  brand VARCHAR(128),
  category_id VARCHAR(64),
  category_path VARCHAR(255),
  listed_at DATE COMMENT 'product listed date',

  first_seen_at DATETIME COMMENT 'first seen time',
  last_seen_at DATETIME COMMENT 'last seen time',

  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_platform_product (platform, platform_product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='market product';

CREATE TABLE market_product_snapshot (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,

  platform VARCHAR(20) NOT NULL,
  platform_product_id VARCHAR(64) NOT NULL,

  snapshot_date DATE NOT NULL COMMENT 'snapshot date',

  price DECIMAL(10,2),
  rating DECIMAL(3,2),
  review_count INT,

  sold_count INT,
  sold_sum DECIMAL(14,2),
  gmv_sum DECIMAL(14,2),
  avg_price DECIMAL(10,2),
  avg_gmv DECIMAL(10,2),
  views INT,
  session_count INT,
  conv_to_cart DECIMAL(8,4),
  conv_view_to_order DECIMAL(8,4),
  stock INT,
  fbo_stock INT,
  fbs_stock INT,
  cb_stock INT,
  retail_stock INT,
  sales_dynamics DECIMAL(10,2),
  min_seller_price DECIMAL(10,2),
  sales_period VARCHAR(16),
  sales_update_at DATETIME,
  item_payload_json LONGTEXT,
  benchmark_json LONGTEXT,

  availability_status VARCHAR(32) COMMENT 'in_stock / out_of_stock',
  stock_hint VARCHAR(64) COMMENT 'stock hint',

  category_rank INT COMMENT 'category rank',
  search_rank INT COMMENT 'search rank',

  data_source VARCHAR(32) COMMENT 'category_page / search_page / detail_page',

  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uk_product_day (platform, platform_product_id, snapshot_date),
  KEY idx_product_time (platform_product_id, snapshot_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='market product snapshot';
