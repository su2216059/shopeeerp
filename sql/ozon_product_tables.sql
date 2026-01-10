-- =====================================================
-- Ozon 商品信息相关表
-- 根据 Ozon API 返回的 JSON 结构创建
-- =====================================================

-- 1. 商品主表 (ozon_product)
-- 对应 JSON 中的 items 数组中的主要字段
DROP TABLE IF EXISTS ozon_product;
CREATE TABLE ozon_product (
    id BIGINT PRIMARY KEY COMMENT '商品ID',
    offer_id VARCHAR(255) COMMENT '商家SKU/货号',
    name VARCHAR(500) COMMENT '商品名称',
    sku BIGINT COMMENT 'Ozon SKU',
    description_category_id INT COMMENT '描述分类ID',
    type_id INT COMMENT '商品类型ID',
    currency_code VARCHAR(10) COMMENT '货币代码',
    price VARCHAR(50) COMMENT '当前价格',
    old_price VARCHAR(50) COMMENT '原价',
    min_price VARCHAR(50) COMMENT '最低价格',
    vat VARCHAR(20) COMMENT '增值税',
    volume_weight DECIMAL(15,4) COMMENT '体积重量',
    discounted_fbo_stocks INT DEFAULT 0 COMMENT '折扣FBO库存数量',
    is_archived TINYINT(1) DEFAULT 0 COMMENT '是否已归档',
    is_autoarchived TINYINT(1) DEFAULT 0 COMMENT '是否自动归档',
    is_discounted TINYINT(1) DEFAULT 0 COMMENT '是否有折扣',
    is_kgt TINYINT(1) DEFAULT 0 COMMENT '是否为大件商品(KGT)',
    is_prepayment_allowed TINYINT(1) DEFAULT 0 COMMENT '是否允许预付款',
    is_super TINYINT(1) DEFAULT 0 COMMENT '是否为超级商品',
    created_at DATETIME COMMENT 'Ozon商品创建时间',
    updated_at DATETIME COMMENT 'Ozon商品更新时间',
    sync_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '本地同步时间',
    INDEX idx_offer_id (offer_id),
    INDEX idx_sku (sku),
    INDEX idx_created_at (created_at),
    INDEX idx_updated_at (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Ozon商品主表';


-- 2. 商品图片表 (ozon_product_image)
-- 对应 JSON 中的 primary_image 数组
DROP TABLE IF EXISTS ozon_product_image;
CREATE TABLE ozon_product_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    image_url VARCHAR(1000) NOT NULL COMMENT '图片URL',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    is_primary TINYINT(1) DEFAULT 1 COMMENT '是否为主图',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product_id (product_id),
    CONSTRAINT fk_image_product FOREIGN KEY (product_id) REFERENCES ozon_product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Ozon商品图片表';


-- 3. 商品状态表 (ozon_product_status)
-- 对应 JSON 中的 statuses 对象
DROP TABLE IF EXISTS ozon_product_status;
CREATE TABLE ozon_product_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    product_id BIGINT NOT NULL UNIQUE COMMENT '商品ID',
    is_created TINYINT(1) DEFAULT 0 COMMENT '是否已创建',
    moderate_status VARCHAR(50) COMMENT '审核状态',
    status VARCHAR(50) COMMENT '商品状态',
    status_description VARCHAR(500) COMMENT '状态描述',
    status_failed VARCHAR(500) COMMENT '失败状态说明',
    status_name VARCHAR(100) COMMENT '状态名称',
    status_tooltip VARCHAR(500) COMMENT '状态提示信息',
    status_updated_at DATETIME COMMENT '状态更新时间',
    validation_status VARCHAR(50) COMMENT '验证状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_product_id (product_id),
    INDEX idx_status (status),
    INDEX idx_moderate_status (moderate_status),
    CONSTRAINT fk_status_product FOREIGN KEY (product_id) REFERENCES ozon_product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Ozon商品状态表';


-- 4. 商品库存汇总表 (ozon_product_stock_summary)
-- 对应 JSON 中的 stocks 对象的 has_stock 字段
DROP TABLE IF EXISTS ozon_product_stock_summary;
CREATE TABLE ozon_product_stock_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    product_id BIGINT NOT NULL UNIQUE COMMENT '商品ID',
    has_stock TINYINT(1) DEFAULT 0 COMMENT '是否有库存',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_product_id (product_id),
    INDEX idx_has_stock (has_stock),
    CONSTRAINT fk_stock_summary_product FOREIGN KEY (product_id) REFERENCES ozon_product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Ozon商品库存汇总表';


-- 5. 商品库存明细表 (ozon_product_stock)
-- 对应 JSON 中的 stocks.stocks 数组
DROP TABLE IF EXISTS ozon_product_stock;
CREATE TABLE ozon_product_stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sku BIGINT COMMENT '库存SKU',
    source VARCHAR(100) COMMENT '库存来源/仓库',
    present INT DEFAULT 0 COMMENT '可用库存数量',
    reserved INT DEFAULT 0 COMMENT '预留库存数量',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_product_id (product_id),
    INDEX idx_sku (sku),
    INDEX idx_source (source),
    UNIQUE KEY uk_product_source (product_id, source),
    CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES ozon_product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Ozon商品库存明细表';


-- =====================================================
-- 视图：商品完整信息视图
-- =====================================================
DROP VIEW IF EXISTS v_ozon_product_full;
CREATE VIEW v_ozon_product_full AS
SELECT 
    p.id,
    p.offer_id,
    p.name,
    p.sku,
    p.description_category_id,
    p.type_id,
    p.currency_code,
    p.price,
    p.old_price,
    p.min_price,
    p.vat,
    p.volume_weight,
    p.discounted_fbo_stocks,
    p.is_archived,
    p.is_autoarchived,
    p.is_discounted,
    p.is_kgt,
    p.is_prepayment_allowed,
    p.is_super,
    p.created_at,
    p.updated_at,
    -- 状态信息
    s.is_created AS status_is_created,
    s.moderate_status,
    s.status,
    s.status_description,
    s.status_failed,
    s.status_name,
    s.status_tooltip,
    s.status_updated_at,
    s.validation_status,
    -- 库存汇总
    ss.has_stock,
    -- 库存统计
    COALESCE(st.total_present, 0) AS total_present,
    COALESCE(st.total_reserved, 0) AS total_reserved
FROM ozon_product p
LEFT JOIN ozon_product_status s ON p.id = s.product_id
LEFT JOIN ozon_product_stock_summary ss ON p.id = ss.product_id
LEFT JOIN (
    SELECT 
        product_id,
        SUM(present) AS total_present,
        SUM(reserved) AS total_reserved
    FROM ozon_product_stock
    GROUP BY product_id
) st ON p.id = st.product_id;
