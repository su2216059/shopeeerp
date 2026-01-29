-- RBAC permissions & role_permissions tables
CREATE TABLE IF NOT EXISTS permissions (
  permission_id BIGINT NOT NULL AUTO_INCREMENT,
  code VARCHAR(64) NOT NULL,
  name VARCHAR(100) NULL,
  description VARCHAR(255) NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (permission_id),
  UNIQUE KEY uk_permissions_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS role_permissions (
  id BIGINT NOT NULL AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_role_permission (role_id, permission_id),
  CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
  CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO permissions (code, name, description) VALUES
('ORDER_VIEW', 'Order View', 'View orders'),
('ORDER_CREATE', 'Order Create', 'Create orders'),
('ORDER_UPDATE', 'Order Update', 'Update orders'),
('ORDER_DELETE', 'Order Delete', 'Delete orders'),
('CUSTOMER_VIEW', 'Customer View', 'View customers'),
('CUSTOMER_CREATE', 'Customer Create', 'Create customers'),
('CUSTOMER_UPDATE', 'Customer Update', 'Update customers'),
('CUSTOMER_DELETE', 'Customer Delete', 'Delete customers'),
('CUSTOMER_SUPPORT_VIEW', 'Customer Support View', 'View customer supports'),
('CUSTOMER_SUPPORT_CREATE', 'Customer Support Create', 'Create customer supports'),
('CUSTOMER_SUPPORT_UPDATE', 'Customer Support Update', 'Update customer supports'),
('CUSTOMER_SUPPORT_DELETE', 'Customer Support Delete', 'Delete customer supports'),
('INVOICE_VIEW', 'Invoice View', 'View invoices'),
('INVOICE_CREATE', 'Invoice Create', 'Create invoices'),
('INVOICE_UPDATE', 'Invoice Update', 'Update invoices'),
('INVOICE_DELETE', 'Invoice Delete', 'Delete invoices'),
('OZON_PRODUCT_VIEW', 'Ozon Product View', 'View Ozon products'),
('OZON_PRODUCT_SYNC', 'Ozon Product Sync', 'Sync Ozon products'),
('OZON_ORDER_VIEW', 'Ozon Order View', 'View Ozon orders'),
('OZON_ORDER_UPDATE', 'Ozon Order Update', 'Update Ozon orders'),
('OZON_ORDER_SYNC', 'Ozon Order Sync', 'Sync Ozon orders'),
('OZON_PROFIT_SYNC', 'Ozon Profit Sync', 'Sync Ozon profit data'),
('OZON_CASHFLOW_SYNC', 'Ozon Cashflow Sync', 'Sync Ozon cashflows'),
('OZON_WAREHOUSE_VIEW', 'Ozon Warehouse View', 'View Ozon warehouses'),
('OZON_WAREHOUSE_SYNC', 'Ozon Warehouse Sync', 'Sync Ozon warehouses'),
('MARKET_VIEW', 'Market View', 'View market signals'),
('MARKET_CALCULATE', 'Market Calculate', 'Calculate market metrics'),
('SHOP_VIEW', 'Shop View', 'View shops'),
('SHOP_CREATE', 'Shop Create', 'Create shops'),
('SHOP_UPDATE', 'Shop Update', 'Update shops'),
('SHOP_DELETE', 'Shop Delete', 'Delete shops'),
('SHOP_CREDENTIAL', 'Shop Credential', 'Manage shop credentials'),
('SHOP_ACCOUNT', 'Shop Account', 'Manage shop accounts'),
('USER_MANAGE', 'User Manage', 'Manage users'),
('ROLE_MANAGE', 'Role Manage', 'Manage roles')
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description);

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT 1, permission_id FROM permissions;

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT 2, permission_id FROM permissions
WHERE code IN ('ORDER_VIEW', 'SHOP_VIEW');
