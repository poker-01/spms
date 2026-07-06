-- 1. 添加管理员端业主管理菜单/按钮权限
INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_path, permission_component, permission_str, sort_order, visible, is_deleted, version)
VALUES
  ('owner:manage:menu', '业主管理', 1, 3, '/admin/owners', NULL, 'owner:query', 5, 1, 0, 0);

SET @owner_manage_id = (SELECT id FROM sys_permission_info WHERE permission_code = 'owner:manage:menu' AND is_deleted = 0 LIMIT 1);

INSERT IGNORE INTO sys_permission_info (permission_code, permission_name, permission_type, parent_id, permission_path, permission_component, permission_str, sort_order, visible, is_deleted, version)
VALUES
  ('owner:query', '查询业主', 2, @owner_manage_id, NULL, NULL, 'owner:query', 1, 0, 0, 0),
  ('owner:add', '新增业主', 2, @owner_manage_id, NULL, NULL, 'owner:add', 2, 0, 0, 0),
  ('owner:edit', '编辑业主', 2, @owner_manage_id, NULL, NULL, 'owner:edit', 3, 0, 0, 0),
  ('owner:delete', '删除业主', 2, @owner_manage_id, NULL, NULL, 'owner:delete', 4, 0, 0, 0);

-- 2. 为超级管理员（角色ID=1）分配新权限
INSERT IGNORE INTO sys_role_permission (role_info_id, permission_info_id, is_deleted, version)
SELECT 1, p.id, 0, 0
FROM sys_permission_info p
WHERE p.permission_code IN ('owner:manage:menu', 'owner:query', 'owner:add', 'owner:edit', 'owner:delete')
  AND p.is_deleted = 0;

-- 3. 为管理员（角色ID=2）分配新权限
INSERT IGNORE INTO sys_role_permission (role_info_id, permission_info_id, is_deleted, version)
SELECT 2, p.id, 0, 0
FROM sys_permission_info p
WHERE p.permission_code IN ('owner:manage:menu', 'owner:query', 'owner:add', 'owner:edit', 'owner:delete')
  AND p.is_deleted = 0;
