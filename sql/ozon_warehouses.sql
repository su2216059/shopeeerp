-- Ozon warehouses and delivery methods
DROP TABLE IF EXISTS ozon_delivery_method;
DROP TABLE IF EXISTS ozon_warehouse;

CREATE TABLE ozon_warehouse (
    warehouse_id BIGINT PRIMARY KEY COMMENT 'warehouse id',
    name VARCHAR(255) COMMENT 'warehouse name',
    store_name VARCHAR(100) COMMENT 'store name',
    status VARCHAR(50) COMMENT 'status',
    working_days VARCHAR(1000) COMMENT 'working days list',
    min_working_days INT COMMENT 'min working days',
    postings_limit INT COMMENT 'postings limit',
    min_postings_limit INT COMMENT 'min postings limit',
    is_rfbs TINYINT(1) DEFAULT 0 COMMENT 'rfbs',
    has_entrusted_acceptance TINYINT(1) DEFAULT 0 COMMENT 'entrusted acceptance',
    can_print_act_in_advance TINYINT(1) DEFAULT 0 COMMENT 'print act in advance',
    has_postings_limit TINYINT(1) DEFAULT 0 COMMENT 'has postings limit',
    is_karantin TINYINT(1) DEFAULT 0 COMMENT 'is karantin',
    is_kgt TINYINT(1) DEFAULT 0 COMMENT 'is kgt',
    is_timetable_editable TINYINT(1) DEFAULT 0 COMMENT 'timetable editable',
    first_mile_type_json LONGTEXT COMMENT 'first mile type json',
    created_at DATETIME COMMENT 'created at',
    updated_at DATETIME COMMENT 'updated at',
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ozon warehouse';

CREATE TABLE ozon_delivery_method (
    id BIGINT PRIMARY KEY COMMENT 'delivery method id',
    warehouse_id BIGINT COMMENT 'warehouse id',
    name VARCHAR(255) COMMENT 'delivery method name',
    status VARCHAR(50) COMMENT 'status',
    cutoff VARCHAR(20) COMMENT 'cutoff',
    provider_id BIGINT COMMENT 'provider id',
    company_id BIGINT COMMENT 'company id',
    template_id BIGINT COMMENT 'template id',
    sla_cut_in BIGINT COMMENT 'sla cut in',
    created_at DATETIME COMMENT 'created at',
    updated_at DATETIME COMMENT 'updated at',
    INDEX idx_warehouse_id (warehouse_id),
    CONSTRAINT fk_delivery_method_warehouse FOREIGN KEY (warehouse_id)
        REFERENCES ozon_warehouse(warehouse_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ozon delivery method';