-- =====================================================
-- Ozon product tables (from Ozon API JSON)
-- =====================================================

-- 1. Product table (ozon_product)
DROP TABLE IF EXISTS ozon_product;
CREATE TABLE ozon_product (
    id BIGINT PRIMARY KEY,
    shop_id BIGINT NOT NULL COMMENT 'shop id',
    offer_id VARCHAR(255),
    name VARCHAR(500),
    sku BIGINT,
    description_category_id INT,
    type_id INT,
    currency_code VARCHAR(10),
    price VARCHAR(50),
    old_price VARCHAR(50),
    min_price VARCHAR(50),
    vat VARCHAR(20),
    volume_weight DECIMAL(15,4),
    discounted_fbo_stocks INT DEFAULT 0,
    is_archived TINYINT(1) DEFAULT 0,
    is_autoarchived TINYINT(1) DEFAULT 0,
    is_discounted TINYINT(1) DEFAULT 0,
    is_kgt TINYINT(1) DEFAULT 0,
    is_prepayment_allowed TINYINT(1) DEFAULT 0,
    is_super TINYINT(1) DEFAULT 0,
    created_at DATETIME,
    updated_at DATETIME,
    sync_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_offer_id (offer_id),
    INDEX idx_shop_id (shop_id),
    INDEX idx_sku (sku),
    INDEX idx_created_at (created_at),
    INDEX idx_updated_at (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ozon product';


-- 2. Product images (ozon_product_image)
DROP TABLE IF EXISTS ozon_product_image;
CREATE TABLE ozon_product_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(1000) NOT NULL,
    sort_order INT DEFAULT 0,
    is_primary TINYINT(1) DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_product_id (product_id),
    CONSTRAINT fk_image_product FOREIGN KEY (product_id) REFERENCES ozon_product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ozon product images';


-- 3. Product status (ozon_product_status)
DROP TABLE IF EXISTS ozon_product_status;
CREATE TABLE ozon_product_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    is_created TINYINT(1) DEFAULT 0,
    moderate_status VARCHAR(50),
    status VARCHAR(50),
    status_description VARCHAR(500),
    status_failed VARCHAR(500),
    status_name VARCHAR(100),
    status_tooltip VARCHAR(500),
    status_updated_at DATETIME,
    validation_status VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product_id (product_id),
    INDEX idx_status (status),
    INDEX idx_moderate_status (moderate_status),
    CONSTRAINT fk_status_product FOREIGN KEY (product_id) REFERENCES ozon_product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ozon product status';


-- 4. Product stock summary (ozon_product_stock_summary)
DROP TABLE IF EXISTS ozon_product_stock_summary;
CREATE TABLE ozon_product_stock_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    has_stock TINYINT(1) DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product_id (product_id),
    INDEX idx_has_stock (has_stock),
    CONSTRAINT fk_stock_summary_product FOREIGN KEY (product_id) REFERENCES ozon_product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ozon product stock summary';


-- 5. Product stock details (ozon_product_stock)
DROP TABLE IF EXISTS ozon_product_stock;
CREATE TABLE ozon_product_stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku BIGINT,
    source VARCHAR(100),
    present INT DEFAULT 0,
    reserved INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product_id (product_id),
    INDEX idx_sku (sku),
    INDEX idx_source (source),
    UNIQUE KEY uk_product_source (product_id, source),
    CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES ozon_product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ozon product stock';


-- =====================================================
-- View: full product info (v_ozon_product_full)
-- =====================================================
DROP VIEW IF EXISTS v_ozon_product_full;
CREATE VIEW v_ozon_product_full AS
SELECT 
    p.id,
    p.shop_id,
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
    -- status info
    s.is_created AS status_is_created,
    s.moderate_status,
    s.status,
    s.status_description,
    s.status_failed,
    s.status_name,
    s.status_tooltip,
    s.status_updated_at,
    s.validation_status,
    -- stock summary
    ss.has_stock,
    -- stock totals
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
