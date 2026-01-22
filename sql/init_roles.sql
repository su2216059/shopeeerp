-- 插入默认角色数据
INSERT INTO roles (role_id, role_name, description) VALUES
(1, 'admin', '管理员'),
(2, 'user', '普通用户')
ON DUPLICATE KEY UPDATE role_name=VALUES(role_name);
