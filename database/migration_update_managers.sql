-- =====================================================
-- 迁移脚本：更新小区负责人并创建物业管理员用户
-- 日期：2026-07-06
-- 说明：
--   1. 幸福花园小区负责人改为张敢，阳光城小区负责人改为蒋勇
--   2. sys_user_info 新增 community_id 字段（管理员绑定小区）
--   3. 创建张敢和蒋勇两个物业管理员用户，绑定各自小区
--   4. 分配 ROLE_ADMIN 角色（管理员角色，拥有除系统管理外的所有权限）
-- =====================================================

-- Step 1: sys_user_info 新增 community_id 字段
-- 如果字段已存在会报错但不影响后续执行，可以先手动检查或忽略错误
ALTER TABLE `sys_user_info`
    ADD COLUMN `community_id` BIGINT NULL COMMENT '所属小区ID（管理员绑定小区）' AFTER `status`;

-- Step 2: 更新小区负责人信息
UPDATE `community_info` SET `manager_name` = '张敢', `manager_phone` = '13800003333', `update_time` = NOW() WHERE `id` = 1;
UPDATE `community_info` SET `manager_name` = '蒋勇', `manager_phone` = '13800004444', `update_time` = NOW() WHERE `id` = 2;

-- Step 3: 删除原来的物业管理员用户及其角色关联
DELETE FROM `sys_user_role` WHERE `user_info_id` = 4;
DELETE FROM `sys_user_info` WHERE `id` = 4;

-- Step 4: 创建物业管理员用户并绑定小区（使用自增主键）
-- 密码均为 123456（BCrypt加密，与现有 admin 用户密码一致）
-- zhanggan 绑定 community_id=1（幸福花园小区）
-- jiangyong 绑定 community_id=2（阳光城小区）
INSERT INTO `sys_user_info` (`user_name`, `password`, `full_name`, `phone_number`, `email`, `avatar_address`, `status`, `community_id`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES
('zhanggan', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '张敢', '13800003333', NULL, NULL, 1, 1, 0, 0, NOW(), NOW(), 1, NULL),
('jiangyong', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '蒋勇', '13800004444', NULL, NULL, 1, 2, 0, 0, NOW(), NOW(), 1, NULL);

-- Step 5: 分配 ROLE_ADMIN（id=2）角色
-- ROLE_ADMIN 已拥有以下权限（无需额外操作）：
--   - dashboard（系统总览）
--   - community（小区管理：小区信息、楼栋管理、房屋管理、业主管理）
--   - property（物业服务：报修管理、投诉建议）
--   - finance（财务管理：费用项目、账单管理、缴费记录）
--   - 不包括 system（系统管理）
INSERT INTO `sys_user_role` (`user_info_id`, `role_info_id`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`)
SELECT `id`, 2, 0, 0, NOW(), NOW(), 1, NULL
FROM `sys_user_info`
WHERE `user_name` IN ('zhanggan', 'jiangyong');
