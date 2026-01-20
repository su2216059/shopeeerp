-- 销量估算结果表
DROP TABLE IF EXISTS market_sales_estimate;

CREATE TABLE market_sales_estimate (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,

  platform VARCHAR(20) NOT NULL,
  platform_product_id VARCHAR(64) NOT NULL,

  period_type VARCHAR(16) COMMENT 'daily / weekly / monthly',
  period_start DATE NOT NULL,
  period_end DATE NOT NULL,

  estimated_sales_min INT COMMENT '估算销量下限',
  estimated_sales_max INT COMMENT '估算销量上限',
  estimated_sales_mid INT COMMENT '中位估算值（展示用）',

  estimation_model VARCHAR(32) COMMENT 'v1_review_rank / v2_stock',
  confidence_score INT COMMENT '0-100',

  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uk_product_period (
    platform,
    platform_product_id,
    period_type,
    period_start,
    period_end
  ),
  KEY idx_platform_period (platform, period_type, period_start)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品销量估算结果';

-- 趋势信号表
DROP TABLE IF EXISTS market_trend_signal;

CREATE TABLE market_trend_signal (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,

  platform VARCHAR(20),
  platform_product_id VARCHAR(64),

  signal_date DATE,

  trend_7d DECIMAL(6,2) COMMENT '7天增长率 %',
  trend_30d DECIMAL(6,2) COMMENT '30天增长率 %',

  rank_change_7d INT COMMENT '7天排名变化',
  review_velocity DECIMAL(6,2) COMMENT '评论增长速度（每日）',

  stock_risk_level VARCHAR(16) COMMENT 'low / medium / high',

  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uk_product_signal (platform, platform_product_id, signal_date),
  KEY idx_platform_date (platform, signal_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='市场趋势信号';
