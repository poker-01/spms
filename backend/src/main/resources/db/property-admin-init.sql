-- 物业管理角色权限与测试账号初始化
-- 密码与 admin 账号一致（默认 123456）

-- 初始化系统管理菜单/按钮权限（如不存在）
-- 使用用户变量避免 MySQL "You can't specify target table for update in FROM clause" 限制

INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_path, permission_component, permission_str, sort_order, visible, is_deleted, version)
VALUES ('system', '系统管理', 1, 0, '/admin', NULL, NULL, 100, 1, 0, 0);

SET @system_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'system' AND is_deleted = 0 LIMIT 1);

INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_path, permission_component, permission_str, sort_order, visible, is_deleted, version)
VALUES
  ('system:user', '用户管理', 1, @system_id, '/admin/users', '/admin/users', 'system:user:query', 1, 1, 0, 0),
  ('system:role', '角色管理', 1, @system_id, '/admin/roles', '/admin/roles', 'system:role:query', 2, 1, 0, 0),
  ('system:permission', '菜单权限', 1, @system_id, '/admin/permissions', '/admin/permissions', 'system:permission:query', 3, 1, 0, 0);

SET @user_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'system:user' AND is_deleted = 0 LIMIT 1);
SET @role_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'system:role' AND is_deleted = 0 LIMIT 1);
SET @permission_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'system:permission' AND is_deleted = 0 LIMIT 1);

INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_path, permission_component, permission_str, sort_order, visible, is_deleted, version)
VALUES
  ('system:user:add', '新增用户', 2, @user_id, NULL, NULL, 'system:user:add', 1, 0, 0, 0),
  ('system:user:edit', '编辑用户', 2, @user_id, NULL, NULL, 'system:user:edit', 2, 0, 0, 0),
  ('system:user:delete', '删除用户', 2, @user_id, NULL, NULL, 'system:user:delete', 3, 0, 0, 0),
  ('system:user:assign', '分配角色', 2, @user_id, NULL, NULL, 'system:user:assign', 4, 0, 0, 0),
  ('system:role:add', '新增角色', 2, @role_id, NULL, NULL, 'system:role:add', 1, 0, 0, 0),
  ('system:role:edit', '编辑角色', 2, @role_id, NULL, NULL, 'system:role:edit', 2, 0, 0, 0),
  ('system:role:delete', '删除角色', 2, @role_id, NULL, NULL, 'system:role:delete', 3, 0, 0, 0),
  ('system:role:assign', '分配权限', 2, @role_id, NULL, NULL, 'system:role:assign', 4, 0, 0, 0),
  ('system:permission:add', '新增权限', 2, @permission_id, NULL, NULL, 'system:permission:add', 1, 0, 0, 0),
  ('system:permission:edit', '编辑权限', 2, @permission_id, NULL, NULL, 'system:permission:edit', 2, 0, 0, 0),
  ('system:permission:delete', '删除权限', 2, @permission_id, NULL, NULL, 'system:permission:delete', 3, 0, 0, 0);

-- 为 ROLE_SUPER_ADMIN 分配全部权限（系统管理+业务）
INSERT IGNORE INTO sys_role_permission (role_info_id, permission_info_id, is_deleted, version)
SELECT 1, p.id, 0, 0
FROM sys_permission_info p
WHERE p.is_deleted = 0;

-- 为 ROLE_ADMIN 分配业务管理权限（不含系统管理）
INSERT IGNORE INTO sys_role_permission (role_info_id, permission_info_id, is_deleted, version)
SELECT 2, p.id, 0, 0
FROM sys_permission_info p
WHERE p.is_deleted = 0
  AND p.permission_code IN (
    'dashboard',
    'community', 'community:community', 'community:building', 'community:house', 'community:owner',
    'property', 'property:repair', 'property:complaint',
    'finance', 'finance:fee', 'finance:bill', 'finance:payment'
  );

-- 物业管理员测试账号（复用 admin 的密码哈希）
INSERT INTO sys_user_info (user_name, password, full_name, phone_number, status, is_deleted, version)
SELECT
  'property',
  (SELECT password FROM sys_user_info WHERE user_name = 'admin' AND is_deleted = 0 LIMIT 1),
  CONVERT(UNHEX('E789A9E4B89AE7AEA1E79086E59198') USING utf8mb4),
  '13800000001',
  1,
  0,
  0
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_user_info WHERE user_name = 'property' AND is_deleted = 0
);

-- 绑定物业管理员角色
INSERT INTO sys_user_role (user_info_id, role_info_id, is_deleted, version)
SELECT u.id, 2, 0, 0
FROM sys_user_info u
WHERE u.user_name = 'property'
  AND u.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1
    FROM sys_user_role ur
    WHERE ur.user_info_id = u.id
      AND ur.role_info_id = 2
      AND ur.is_deleted = 0
  );

-- 初始化业主角色（如不存在）
INSERT IGNORE INTO sys_role_info (id, role_code, role_name, role_type, is_deleted, version)
VALUES (3, 'ROLE_OWNER', '业主', 2, 0, 0);

-- 初始化业主端菜单/按钮权限
INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_path, permission_component, permission_str, sort_order, visible, is_deleted, version)
VALUES ('owner', '业主服务', 1, 0, '/owner', NULL, NULL, 10, 1, 0, 0);

SET @owner_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'owner' AND is_deleted = 0 LIMIT 1);

INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_path, permission_component, permission_str, sort_order, visible, is_deleted, version)
VALUES
  ('owner:bill', '我的账单', 1, @owner_id, '/owner/bills', '/owner/bills', 'owner:bill:query', 1, 1, 0, 0),
  ('owner:repair', '报修申请', 1, @owner_id, '/owner/repairs', '/owner/repairs', 'owner:repair:query', 2, 1, 0, 0),
  ('owner:complaint', '投诉建议', 1, @owner_id, '/owner/complaints', '/owner/complaints', 'owner:complaint:query', 3, 1, 0, 0);

SET @owner_bill_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'owner:bill' AND is_deleted = 0 LIMIT 1);
SET @owner_repair_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'owner:repair' AND is_deleted = 0 LIMIT 1);
SET @owner_complaint_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'owner:complaint' AND is_deleted = 0 LIMIT 1);

INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_path, permission_component, permission_str, sort_order, visible, is_deleted, version)
VALUES
  ('owner:bill:pay', '缴纳账单', 2, @owner_bill_id, NULL, NULL, 'owner:bill:pay', 1, 0, 0, 0),
  ('owner:repair:add', '提交报修', 2, @owner_repair_id, NULL, NULL, 'owner:repair:add', 1, 0, 0, 0),
  ('owner:complaint:add', '提交投诉', 2, @owner_complaint_id, NULL, NULL, 'owner:complaint:add', 1, 0, 0, 0);

-- 为 ROLE_OWNER 分配业主端权限
INSERT IGNORE INTO sys_role_permission (role_info_id, permission_info_id, is_deleted, version)
SELECT 3, p.id, 0, 0
FROM sys_permission_info p
WHERE p.is_deleted = 0
  AND p.permission_code LIKE 'owner%';

-- 业主测试账号（复用 admin 的密码哈希）
INSERT INTO sys_user_info (user_name, password, full_name, phone_number, status, is_deleted, version)
SELECT
  'owner1',
  (SELECT password FROM sys_user_info WHERE user_name = 'admin' AND is_deleted = 0 LIMIT 1),
  CONVERT(UNHEX('E5BCA0E4B889') USING utf8mb4),
  '13800010001',
  1,
  0,
  0
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_user_info WHERE user_name = 'owner1' AND is_deleted = 0
);

INSERT INTO sys_user_info (user_name, password, full_name, phone_number, status, is_deleted, version)
SELECT
  'owner2',
  (SELECT password FROM sys_user_info WHERE user_name = 'admin' AND is_deleted = 0 LIMIT 1),
  CONVERT(UNHEX('E69D8EE59B9B') USING utf8mb4),
  '13800010002',
  1,
  0,
  0
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_user_info WHERE user_name = 'owner2' AND is_deleted = 0
);

-- 绑定业主角色
INSERT INTO sys_user_role (user_info_id, role_info_id, is_deleted, version)
SELECT u.id, 3, 0, 0
FROM sys_user_info u
WHERE u.user_name IN ('owner1', 'owner2')
  AND u.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1
    FROM sys_user_role ur
    WHERE ur.user_info_id = u.id
      AND ur.role_info_id = 3
      AND ur.is_deleted = 0
  );

-- 业主档案（如不存在）
INSERT INTO owner_info (owner_name, owner_phone, id_card, gender, status, is_deleted, version)
SELECT '张三', '13800010001', '110101199001011234', 1, 1, 0, 0
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM owner_info WHERE owner_phone = '13800010001' AND is_deleted = 0
);

INSERT INTO owner_info (owner_name, owner_phone, id_card, gender, status, is_deleted, version)
SELECT '李四', '13800010002', '110101199002022345', 1, 1, 0, 0
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM owner_info WHERE owner_phone = '13800010002' AND is_deleted = 0
);
