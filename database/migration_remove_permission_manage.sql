-- 删除"菜单权限"菜单项及其子权限
-- 涉及权限: id=237 (菜单权限), id=246,247,248 (新增/编辑/删除权限)

-- 1. 先删除角色-权限关联（超级管理员 role_id=1 拥有的这些权限）
DELETE FROM sys_role_permission WHERE permission_info_id IN (237, 246, 247, 248);

-- 2. 逻辑删除 sys_permission_info 中的记录
UPDATE sys_permission_info SET is_deleted = 1 WHERE id IN (237, 246, 247, 248);
