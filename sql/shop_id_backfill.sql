-- Backfill shop_id using default shop (fallback to 1).
SET @default_shop_id := (SELECT id FROM shop WHERE is_default = 1 LIMIT 1);
SET @default_shop_id := IFNULL(@default_shop_id, 1);

UPDATE customers
SET shop_id = @default_shop_id
WHERE shop_id IS NULL OR shop_id = 0;

UPDATE customer_support
SET shop_id = @default_shop_id
WHERE shop_id IS NULL OR shop_id = 0;

UPDATE orders
SET shop_id = @default_shop_id
WHERE shop_id IS NULL OR shop_id = 0;

UPDATE invoices
SET shop_id = @default_shop_id
WHERE shop_id IS NULL OR shop_id = 0;

UPDATE ozon_warehouse
SET shop_id = @default_shop_id
WHERE shop_id IS NULL OR shop_id = 0;

UPDATE ozon_product
SET shop_id = @default_shop_id
WHERE shop_id IS NULL OR shop_id = 0;
