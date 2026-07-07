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

-- =====================================================
-- 补全业务模块 CRUD 按钮权限（Controller @PreAuthorize 所需）
-- =====================================================

-- 1) 修复"系统总览"菜单：路径指向 /admin/home，确保可点击导航
UPDATE sys_permission_info SET permission_path = '/admin/home', permission_str = 'dashboard:view'
WHERE permission_code = 'dashboard' AND is_deleted = 0 AND (permission_path = '/dashboard' OR permission_str IS NULL OR permission_str = '');

-- 隐藏旧版系统管理子菜单（已被 235-237 替代），避免菜单重复
UPDATE sys_permission_info SET visible = 0
WHERE id IN (21, 22, 23) AND is_deleted = 0;

-- 隐藏旧版财务管理子菜单（已被 finance:fee:menu / finance:bill:menu 替代）及缴费记录菜单
UPDATE sys_permission_info SET visible = 0
WHERE id IN (51, 52, 53) AND is_deleted = 0;
UPDATE sys_permission_info SET visible = 0
WHERE permission_code = 'finance:payment:menu' AND is_deleted = 0;

-- 为已有菜单级权限补上 permission_str（用于 query 检查）
UPDATE sys_permission_info SET permission_str = 'community:query' WHERE permission_code = 'community:community' AND is_deleted = 0 AND (permission_str IS NULL OR permission_str = '');
UPDATE sys_permission_info SET permission_str = 'building:query' WHERE permission_code = 'community:building' AND is_deleted = 0 AND (permission_str IS NULL OR permission_str = '');
UPDATE sys_permission_info SET permission_str = 'house:query' WHERE permission_code = 'community:house' AND is_deleted = 0 AND (permission_str IS NULL OR permission_str = '');
UPDATE sys_permission_info SET permission_str = 'owner:query' WHERE permission_code = 'community:owner' AND is_deleted = 0 AND (permission_str IS NULL OR permission_str = '');
UPDATE sys_permission_info SET permission_str = 'repair:query' WHERE permission_code = 'property:repair' AND is_deleted = 0 AND (permission_str IS NULL OR permission_str = '');
UPDATE sys_permission_info SET permission_str = 'complaint:query' WHERE permission_code = 'property:complaint' AND is_deleted = 0 AND (permission_str IS NULL OR permission_str = '');
UPDATE sys_permission_info SET permission_str = 'finance:fee:query' WHERE permission_code = 'finance:fee' AND is_deleted = 0 AND (permission_str IS NULL OR permission_str = '');
UPDATE sys_permission_info SET permission_str = 'finance:bill:query' WHERE permission_code = 'finance:bill' AND is_deleted = 0 AND (permission_str IS NULL OR permission_str = '');
UPDATE sys_permission_info SET permission_str = 'finance:payment:query' WHERE permission_code = 'finance:payment' AND is_deleted = 0 AND (permission_str IS NULL OR permission_str = '');

-- 2) 获取各业务菜单的 ID
SET @cc_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'community:community' AND is_deleted = 0 LIMIT 1);
SET @cb_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'community:building' AND is_deleted = 0 LIMIT 1);
SET @ch_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'community:house' AND is_deleted = 0 LIMIT 1);
SET @co_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'community:owner' AND is_deleted = 0 LIMIT 1);
SET @pr_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'property:repair' AND is_deleted = 0 LIMIT 1);
SET @pc_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'property:complaint' AND is_deleted = 0 LIMIT 1);
SET @ff_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'finance:fee' AND is_deleted = 0 LIMIT 1);
SET @fb_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'finance:bill' AND is_deleted = 0 LIMIT 1);
SET @fp_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'finance:payment' AND is_deleted = 0 LIMIT 1);

-- 3) 小区管理按钮权限
INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_str, sort_order, visible, is_deleted, version)
VALUES
  ('community:add', '新增小区', 3, @cc_id, 'community:add', 1, 0, 0, 0),
  ('community:edit', '编辑小区', 3, @cc_id, 'community:edit', 2, 0, 0, 0),
  ('community:delete', '删除小区', 3, @cc_id, 'community:delete', 3, 0, 0, 0);

-- 4) 楼栋管理按钮权限
INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_str, sort_order, visible, is_deleted, version)
VALUES
  ('building:add', '新增楼栋', 3, @cb_id, 'building:add', 1, 0, 0, 0),
  ('building:edit', '编辑楼栋', 3, @cb_id, 'building:edit', 2, 0, 0, 0),
  ('building:delete', '删除楼栋', 3, @cb_id, 'building:delete', 3, 0, 0, 0);

-- 5) 房屋管理按钮权限
INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_str, sort_order, visible, is_deleted, version)
VALUES
  ('house:add', '新增房屋', 3, @ch_id, 'house:add', 1, 0, 0, 0),
  ('house:edit', '编辑房屋', 3, @ch_id, 'house:edit', 2, 0, 0, 0),
  ('house:delete', '删除房屋', 3, @ch_id, 'house:delete', 3, 0, 0, 0);

-- 6) 业主管理按钮权限
INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_str, sort_order, visible, is_deleted, version)
VALUES
  ('owner:add', '新增业主', 3, @co_id, 'owner:add', 1, 0, 0, 0),
  ('owner:edit', '编辑业主', 3, @co_id, 'owner:edit', 2, 0, 0, 0),
  ('owner:delete', '删除业主', 3, @co_id, 'owner:delete', 3, 0, 0, 0);

-- 7) 报修管理按钮权限
INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_str, sort_order, visible, is_deleted, version)
VALUES
  ('repair:assign', '派单处理', 3, @pr_id, 'repair:assign', 1, 0, 0, 0);

-- 8) 投诉建议按钮权限
INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_str, sort_order, visible, is_deleted, version)
VALUES
  ('complaint:reply', '回复投诉', 3, @pc_id, 'complaint:reply', 1, 0, 0, 0),
  ('complaint:close', '关闭投诉', 3, @pc_id, 'complaint:close', 2, 0, 0, 0);

-- 9) 费用项目按钮权限
INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_str, sort_order, visible, is_deleted, version)
VALUES
  ('finance:fee:add', '新增费用项', 3, @ff_id, 'finance:fee:add', 1, 0, 0, 0),
  ('finance:fee:edit', '编辑费用项', 3, @ff_id, 'finance:fee:edit', 2, 0, 0, 0),
  ('finance:fee:delete', '删除费用项', 3, @ff_id, 'finance:fee:delete', 3, 0, 0, 0);

-- 10) 账单管理按钮权限
INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_str, sort_order, visible, is_deleted, version)
VALUES
  ('finance:bill:generate', '生成账单', 3, @fb_id, 'finance:bill:generate', 1, 0, 0, 0),
  ('finance:bill:pay', '确认缴费', 3, @fb_id, 'finance:bill:pay', 2, 0, 0, 0);

-- 初始化财务管理菜单/按钮权限
INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_path, permission_component, permission_str, sort_order, visible, is_deleted, version)
VALUES ('finance:fee:menu', '费用项目管理', 1, 5, '/admin/fee-items', NULL, 'finance:fee:query', 6, 1, 0, 0);

INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_path, permission_component, permission_str, sort_order, visible, is_deleted, version)
VALUES ('finance:bill:menu', '账单管理', 1, 5, '/admin/bills', NULL, 'finance:bill:query', 7, 1, 0, 0);

INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_path, permission_component, permission_str, sort_order, visible, is_deleted, version)
VALUES ('finance:payment:menu', '缴费记录', 1, 5, '/admin/payments', NULL, 'finance:payment:query', 8, 0, 0, 0);

SET @fee_menu_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'finance:fee:menu' AND is_deleted = 0 LIMIT 1);
SET @bill_menu_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'finance:bill:menu' AND is_deleted = 0 LIMIT 1);
SET @payment_menu_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'finance:payment:menu' AND is_deleted = 0 LIMIT 1);

INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_path, permission_component, permission_str, sort_order, visible, is_deleted, version)
VALUES
  ('finance:fee:query', '查询费用项目', 2, @fee_menu_id, NULL, NULL, 'finance:fee:query', 1, 0, 0, 0),
  ('finance:fee:add', '新增费用项目', 2, @fee_menu_id, NULL, NULL, 'finance:fee:add', 2, 0, 0, 0),
  ('finance:fee:edit', '编辑费用项目', 2, @fee_menu_id, NULL, NULL, 'finance:fee:edit', 3, 0, 0, 0),
  ('finance:fee:delete', '删除费用项目', 2, @fee_menu_id, NULL, NULL, 'finance:fee:delete', 4, 0, 0, 0),
  ('finance:bill:query', '查询账单', 2, @bill_menu_id, NULL, NULL, 'finance:bill:query', 1, 0, 0, 0),
  ('finance:bill:generate', '生成账单', 2, @bill_menu_id, NULL, NULL, 'finance:bill:generate', 2, 0, 0, 0),
  ('finance:bill:pay', '缴费登记', 2, @bill_menu_id, NULL, NULL, 'finance:bill:pay', 3, 0, 0, 0),
  ('finance:bill:delete', '删除账单', 2, @bill_menu_id, NULL, NULL, 'finance:bill:delete', 4, 0, 0, 0),
  ('finance:payment:query', '查询缴费记录', 2, @payment_menu_id, NULL, NULL, 'finance:payment:query', 1, 0, 0, 0),
  ('dashboard:view', '数据看板', 2, 0, NULL, NULL, 'dashboard:view', 1, 0, 0, 0);

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
    'dashboard', 'dashboard:view',
    'community', 'community:community', 'community:building', 'community:house', 'community:owner',
    'community:add', 'community:edit', 'community:delete',
    'building:add', 'building:edit', 'building:delete',
    'house:add', 'house:edit', 'house:delete',
    'owner:add', 'owner:edit', 'owner:delete',
    'property', 'property:repair', 'property:complaint',
    'repair:assign', 'complaint:reply', 'complaint:close',
    'finance', 'finance:fee', 'finance:bill', 'finance:payment',
    'finance:fee:menu', 'finance:fee:query', 'finance:fee:add', 'finance:fee:edit', 'finance:fee:delete',
    'finance:bill:menu', 'finance:bill:query', 'finance:bill:generate', 'finance:bill:pay', 'finance:bill:delete',
    'finance:payment:menu', 'finance:payment:query'
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
