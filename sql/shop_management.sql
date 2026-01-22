-- =====================================================
-- 多店铺管理系统 - 数据库表结构
-- =====================================================

-- 1. 店铺表 (核心表)
DROP TABLE IF EXISTS shop;
CREATE TABLE shop (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    
    -- 基本信息
    shop_code VARCHAR(32) NOT NULL COMMENT '店铺编码(唯一标识)',
    shop_name VARCHAR(128) NOT NULL COMMENT '店铺名称',
    platform VARCHAR(32) NOT NULL DEFAULT 'ozon' COMMENT '平台: ozon/shopee/wildberries/amazon',
    market VARCHAR(10) NOT NULL DEFAULT 'RU' COMMENT '市场: RU/CN/US/EU等',
    
    -- 归属
    owner_user_id BIGINT COMMENT '所属用户ID',
    
    -- 店铺状态
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active/suspended/closed',
    is_default TINYINT(1) DEFAULT 0 COMMENT '是否默认店铺',
    
    -- 业务信息
    seller_id VARCHAR(64) COMMENT '平台卖家ID',
    shop_url VARCHAR(255) COMMENT '店铺URL',
    timezone VARCHAR(32) DEFAULT 'Europe/Moscow' COMMENT '店铺时区',
    currency VARCHAR(10) DEFAULT 'RUB' COMMENT '主要货币',
    
    -- 备注
    description TEXT COMMENT '店铺描述',
    
    -- 时间戳
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_shop_code (shop_code),
    KEY idx_owner (owner_user_id),
    KEY idx_platform (platform, market)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店铺信息';


-- 2. 店铺API凭证表 (敏感信息，加密存储)
DROP TABLE IF EXISTS shop_credential;
CREATE TABLE shop_credential (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shop_id BIGINT NOT NULL COMMENT '店铺ID',
    
    -- API凭证 (加密存储)
    client_id VARCHAR(128) COMMENT '平台Client ID',
    api_key_encrypted VARCHAR(512) COMMENT 'API Key (AES加密)',
    api_secret_encrypted VARCHAR(512) COMMENT 'API Secret (AES加密, 部分平台需要)',
    
    -- Token (OAuth类型平台)
    access_token_encrypted VARCHAR(1024) COMMENT 'Access Token (加密)',
    refresh_token_encrypted VARCHAR(1024) COMMENT 'Refresh Token (加密)',
    token_expires_at DATETIME COMMENT 'Token过期时间',
    
    -- 凭证状态
    credential_type VARCHAR(20) DEFAULT 'api_key' COMMENT '凭证类型: api_key/oauth/session',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态: active/expired/revoked',
    last_used_at DATETIME COMMENT '最后使用时间',
    last_verified_at DATETIME COMMENT '最后验证时间',
    
    -- 限流配置
    rate_limit_per_minute INT DEFAULT 60 COMMENT '每分钟请求限制',
    rate_limit_per_day INT DEFAULT 10000 COMMENT '每天请求限制',
    
    -- 时间戳
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_shop (shop_id),
    CONSTRAINT fk_credential_shop FOREIGN KEY (shop_id) REFERENCES shop(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店铺API凭证';


-- 3. 店铺登录账号表 (网页登录、后台登录等)
DROP TABLE IF EXISTS shop_account;
CREATE TABLE shop_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shop_id BIGINT NOT NULL COMMENT '店铺ID',
    
    -- 账号信息
    account_type VARCHAR(32) NOT NULL COMMENT '账号类型: seller_center/warehouse/finance/admin',
    account_name VARCHAR(128) COMMENT '账号名称/描述',
    
    -- 登录凭证 (加密存储)
    username VARCHAR(128) NOT NULL COMMENT '用户名/邮箱/手机号',
    password_encrypted VARCHAR(512) COMMENT '密码 (AES加密)',
    
    -- 二次验证
    two_factor_secret VARCHAR(128) COMMENT '2FA密钥 (如有)',
    security_question TEXT COMMENT '安全问题 (JSON格式)',
    
    -- 状态
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态: active/disabled/locked',
    last_login_at DATETIME COMMENT '最后登录时间',
    login_fail_count INT DEFAULT 0 COMMENT '登录失败次数',
    
    -- 备注
    remark TEXT COMMENT '备注',
    
    -- 时间戳
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    KEY idx_shop (shop_id),
    KEY idx_type (account_type),
    CONSTRAINT fk_account_shop FOREIGN KEY (shop_id) REFERENCES shop(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店铺登录账号';


-- 4. 用户-店铺关联表 (支持多用户协作管理店铺)
DROP TABLE IF EXISTS user_shop;
CREATE TABLE user_shop (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    shop_id BIGINT NOT NULL COMMENT '店铺ID',
    
    -- 权限
    role VARCHAR(32) NOT NULL DEFAULT 'viewer' COMMENT '角色: owner/admin/operator/viewer',
    permissions JSON COMMENT '细粒度权限 (JSON数组)',
    
    -- 时间戳
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_user_shop (user_id, shop_id),
    KEY idx_shop (shop_id),
    CONSTRAINT fk_us_shop FOREIGN KEY (shop_id) REFERENCES shop(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户店铺关联';


-- 5. API调用日志表 (审计和监控)
DROP TABLE IF EXISTS shop_api_log;
CREATE TABLE shop_api_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shop_id BIGINT NOT NULL COMMENT '店铺ID',
    
    -- 请求信息
    api_endpoint VARCHAR(255) NOT NULL COMMENT 'API端点',
    http_method VARCHAR(10) DEFAULT 'POST' COMMENT 'HTTP方法',
    request_id VARCHAR(64) COMMENT '请求ID',
    
    -- 响应信息
    response_code INT COMMENT 'HTTP状态码',
    response_time_ms INT COMMENT '响应时间(毫秒)',
    is_success TINYINT(1) DEFAULT 1 COMMENT '是否成功',
    error_message VARCHAR(512) COMMENT '错误信息',
    
    -- 时间
    called_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    KEY idx_shop_time (shop_id, called_at),
    KEY idx_endpoint (api_endpoint)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='API调用日志';


-- =====================================================
-- 初始数据
-- =====================================================

-- 插入默认店铺 (从现有配置迁移)
INSERT INTO shop (shop_code, shop_name, platform, market, status, is_default, seller_id) VALUES
('OZON_MAIN', '主店铺', 'ozon', 'RU', 'active', 1, '3207535');

-- 插入对应的API凭证 (实际使用时需要加密)
-- 注意: 这里的 api_key 应该用加密后的值，示例中用明文占位
INSERT INTO shop_credential (shop_id, client_id, api_key_encrypted, credential_type, status) VALUES
(1, '3207535', 'ENCRYPTED_API_KEY_HERE', 'api_key', 'active');
