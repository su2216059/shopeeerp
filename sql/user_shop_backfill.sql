-- Backfill user_shop from existing shop ownership
-- This script creates owner relations for shops that already have owner_user_id.
INSERT INTO user_shop (user_id, shop_id, role, permissions, created_at)
SELECT s.owner_user_id, s.id, 'owner', NULL, NOW()
FROM shop s
LEFT JOIN user_shop us
  ON us.user_id = s.owner_user_id
 AND us.shop_id = s.id
WHERE s.owner_user_id IS NOT NULL
  AND us.id IS NULL;
