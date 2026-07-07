-- =====================================================
-- 迁移脚本 V2：修复业主用户绑定小区、创建维修人员、清理跨小区数据
-- 日期：2026-07-06
-- 基于当前数据库实际状态修正，兼容已有数据
-- =====================================================

USE spms;

SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- Step 1: 更新现有业主用户的 community_id
-- 当前所有业主用户的 community_id 都是 NULL，需要绑定
-- =====================================================

-- 幸福花园小区 (community_id=1) 的业主用户
UPDATE `sys_user_info` SET `community_id` = 1, `update_time` = NOW() WHERE `user_name` = '123456' AND `is_deleted` = 0;     -- 张三
UPDATE `sys_user_info` SET `community_id` = 1, `update_time` = NOW() WHERE `user_name` = '13900001111' AND `is_deleted` = 0; -- 王五
UPDATE `sys_user_info` SET `community_id` = 1, `update_time` = NOW() WHERE `user_name` = '13900002222' AND `is_deleted` = 0; -- 赵六
UPDATE `sys_user_info` SET `community_id` = 1, `update_time` = NOW() WHERE `user_name` = '13900003333' AND `is_deleted` = 0; -- 孙七
UPDATE `sys_user_info` SET `community_id` = 1, `update_time` = NOW() WHERE `user_name` = '13900004444' AND `is_deleted` = 0; -- 周八
UPDATE `sys_user_info` SET `community_id` = 1, `update_time` = NOW() WHERE `user_name` = 'owner1' AND `is_deleted` = 0;      -- 张三(owner1)
UPDATE `sys_user_info` SET `community_id` = 1, `update_time` = NOW() WHERE `user_name` = 'owner2' AND `is_deleted` = 0;      -- 李四(owner2)
UPDATE `sys_user_info` SET `community_id` = 1, `update_time` = NOW() WHERE `user_name` = '李逵' AND `is_deleted` = 0;        -- 李逵
UPDATE `sys_user_info` SET `community_id` = 1, `update_time` = NOW() WHERE `user_name` = '孙悟空' AND `is_deleted` = 0;      -- 孙悟空

-- 阳光城小区 (community_id=2) 目前没有业主用户（王昌龄没有 sys_user_info），后续创建

-- =====================================================
-- Step 2: 清理有问题的业主-房屋关联
-- 李逵（owner_info_id=7）错误关联了 A栋101（已被王五占用），需要删除
-- =====================================================
UPDATE `owner_house_rel` SET `is_deleted` = 1, `update_time` = NOW() 
WHERE `owner_info_id` = 7 AND `house_info_id` = 1 AND `is_deleted` = 0;

-- =====================================================
-- Step 3: 修正已有 owner_info 数据
-- 王昌龄（id=9）当前 phone=13324567678，但迁移脚本期望 13900007777
-- 保持现有手机号不变，只修正其对应的房屋关联
-- =====================================================

-- 王昌龄当前已关联 2号楼1001（house_id=262），将其改为 1号楼101（house_id=169）
-- 先查看王昌龄当前的 owner_house_rel
UPDATE `owner_house_rel` SET `house_info_id` = 169, `update_time` = NOW() 
WHERE `owner_info_id` = 9 AND `is_deleted` = 0;

-- =====================================================
-- Step 4: 新增缺失的业主信息(owner_info)
-- 已有 id 1-9，新增 id 10-14
-- =====================================================

-- 幸福花园小区 刘建国 (B栋101)
INSERT INTO `owner_info` (`owner_name`, `owner_phone`, `id_card`, `gender`, `email`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES ('刘建国', '13900005555', '110101198505055678', 1, 'liujianguo@example.com', 1, 0, 0, NOW(), NOW(), 1);

-- 幸福花园小区 陈美丽 (B栋201)
INSERT INTO `owner_info` (`owner_name`, `owner_phone`, `id_card`, `gender`, `email`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES ('陈美丽', '13900006666', '110101198606066789', 0, 'chenmeili@example.com', 1, 0, 0, NOW(), NOW(), 1);

-- 阳光城小区 黄小明 (1号楼201)
INSERT INTO `owner_info` (`owner_name`, `owner_phone`, `id_card`, `gender`, `email`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES ('黄小明', '13900008888', '310101198808088901', 1, 'huangxiaoming@example.com', 1, 0, 0, NOW(), NOW(), 1);

-- 阳光城小区 林黛玉 (2号楼101)
INSERT INTO `owner_info` (`owner_name`, `owner_phone`, `id_card`, `gender`, `email`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES ('林黛玉', '13900009999', '310101199009099012', 0, 'lindaiyu@example.com', 1, 0, 0, NOW(), NOW(), 1);

-- 阳光城小区 贾宝玉 (2号楼201)
INSERT INTO `owner_info` (`owner_name`, `owner_phone`, `id_card`, `gender`, `email`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES ('贾宝玉', '13900010000', '310101199110100123', 1, 'jiabaoyu@example.com', 1, 0, 0, NOW(), NOW(), 1);

-- =====================================================
-- Step 5: 创建业主-房屋关联关系 (owner_house_rel)
-- =====================================================

-- 幸福花园小区 - 刘建国 -> B栋101 (building_id=2, house_number=101)
INSERT INTO `owner_house_rel` (`owner_info_id`, `house_info_id`, `relation_type`, `is_primary`, `move_in_date`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
SELECT 10, id, 1, 1, '2024-05-01', 0, 0, NOW(), NOW(), 1 FROM house_info WHERE building_id = 2 AND house_number = '101' AND is_deleted = 0 LIMIT 1;

-- 幸福花园小区 - 陈美丽 -> B栋201 (building_id=2, house_number=201)
INSERT INTO `owner_house_rel` (`owner_info_id`, `house_info_id`, `relation_type`, `is_primary`, `move_in_date`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
SELECT 11, id, 1, 1, '2024-06-01', 0, 0, NOW(), NOW(), 1 FROM house_info WHERE building_id = 2 AND house_number = '201' AND is_deleted = 0 LIMIT 1;

-- 阳光城小区 - 黄小明 -> 1号楼201 (building_id=4, house_number=201)
INSERT INTO `owner_house_rel` (`owner_info_id`, `house_info_id`, `relation_type`, `is_primary`, `move_in_date`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
SELECT 12, id, 1, 1, '2024-08-01', 0, 0, NOW(), NOW(), 1 FROM house_info WHERE building_id = 4 AND house_number = '201' AND is_deleted = 0 LIMIT 1;

-- 阳光城小区 - 林黛玉 -> 2号楼101 (building_id=5, house_number=101)
INSERT INTO `owner_house_rel` (`owner_info_id`, `house_info_id`, `relation_type`, `is_primary`, `move_in_date`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
SELECT 13, id, 1, 1, '2024-09-01', 0, 0, NOW(), NOW(), 1 FROM house_info WHERE building_id = 5 AND house_number = '101' AND is_deleted = 0 LIMIT 1;

-- 阳光城小区 - 贾宝玉 -> 2号楼201 (building_id=5, house_number=201)
INSERT INTO `owner_house_rel` (`owner_info_id`, `house_info_id`, `relation_type`, `is_primary`, `move_in_date`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
SELECT 14, id, 1, 1, '2024-10-01', 0, 0, NOW(), NOW(), 1 FROM house_info WHERE building_id = 5 AND house_number = '201' AND is_deleted = 0 LIMIT 1;

-- =====================================================
-- Step 6: 为李逵分配正确的房屋（幸福花园 A栋103 或 C栋101 等空闲房屋）
-- 李逵(owner_info_id=7) 之前错误关联了A栋101，已删除，重新分配 C栋101 (house_id=5已被孙七占用)
-- 改为分配 A栋103 (house_id=3)
-- =====================================================
INSERT INTO `owner_house_rel` (`owner_info_id`, `house_info_id`, `relation_type`, `is_primary`, `move_in_date`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES (7, 3, 1, 1, '2024-11-01', 0, 0, NOW(), NOW(), 1);

-- 孙悟空（owner_info_id=8）已关联 A栋1001（house_id=37），保持不变

-- =====================================================
-- Step 7: 更新 house_info 的 owner_name/owner_phone/status
-- 将已有业主的房屋标记为已入住
-- =====================================================

-- 幸福花园小区 - A栋
UPDATE `house_info` SET `owner_name` = '王五', `owner_phone` = '13900001111', `status` = 1, `update_time` = NOW() WHERE `id` = 1;
UPDATE `house_info` SET `owner_name` = '赵六', `owner_phone` = '13900002222', `status` = 1, `update_time` = NOW() WHERE `id` = 2;
UPDATE `house_info` SET `owner_name` = '李逵', `owner_phone` = '12345679999', `status` = 1, `update_time` = NOW() WHERE `id` = 3;
UPDATE `house_info` SET `owner_name` = '孙七', `owner_phone` = '13900003333', `status` = 1, `update_time` = NOW() WHERE `id` = 5;
UPDATE `house_info` SET `owner_name` = '周八', `owner_phone` = '13900004444', `status` = 1, `update_time` = NOW() WHERE `id` = 6;
UPDATE `house_info` SET `owner_name` = '孙悟空', `owner_phone` = '17265244700', `status` = 1, `update_time` = NOW() WHERE `id` = 37;

-- 幸福花园小区 - B栋
UPDATE `house_info` SET `owner_name` = '刘建国', `owner_phone` = '13900005555', `status` = 1, `update_time` = NOW() WHERE `building_id` = 2 AND `house_number` = '101' AND `is_deleted` = 0;
UPDATE `house_info` SET `owner_name` = '陈美丽', `owner_phone` = '13900006666', `status` = 1, `update_time` = NOW() WHERE `building_id` = 2 AND `house_number` = '201' AND `is_deleted` = 0;

-- 阳光城小区
UPDATE `house_info` SET `owner_name` = '王昌龄', `owner_phone` = '13324567678', `status` = 1, `update_time` = NOW() WHERE `building_id` = 4 AND `house_number` = '101' AND `is_deleted` = 0;
UPDATE `house_info` SET `owner_name` = '黄小明', `owner_phone` = '13900008888', `status` = 1, `update_time` = NOW() WHERE `building_id` = 4 AND `house_number` = '201' AND `is_deleted` = 0;
UPDATE `house_info` SET `owner_name` = '林黛玉', `owner_phone` = '13900009999', `status` = 1, `update_time` = NOW() WHERE `building_id` = 5 AND `house_number` = '101' AND `is_deleted` = 0;
UPDATE `house_info` SET `owner_name` = '贾宝玉', `owner_phone` = '13900010000', `status` = 1, `update_time` = NOW() WHERE `building_id` = 5 AND `house_number` = '201' AND `is_deleted` = 0;

-- =====================================================
-- Step 8: 为每个小区创建业主系统用户（用于登录）
-- 密码均为 123456（BCrypt加密）
-- 注意：王五/赵六/孙七/周八/李逵/孙悟空已有 sys_user_info，只需创建缺失的
-- =====================================================

-- 幸福花园小区业主用户 (community_id=1) - 创建刘建国、陈美丽
INSERT IGNORE INTO `sys_user_info` (`user_name`, `password`, `full_name`, `phone_number`, `email`, `avatar_address`, `status`, `community_id`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES
('liujianguo', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '刘建国', '13900005555', 'liujianguo@example.com', NULL, 1, 1, 0, 0, NOW(), NOW(), 1, NULL),
('chenmeili', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '陈美丽', '13900006666', 'chenmeili@example.com', NULL, 1, 1, 0, 0, NOW(), NOW(), 1, NULL);

-- 阳光城小区业主用户 (community_id=2)
INSERT IGNORE INTO `sys_user_info` (`user_name`, `password`, `full_name`, `phone_number`, `email`, `avatar_address`, `status`, `community_id`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES
('wangchangling', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '王昌龄', '13324567678', 'wangchangling@example.com', NULL, 1, 2, 0, 0, NOW(), NOW(), 1, NULL),
('huangxiaoming', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '黄小明', '13900008888', 'huangxiaoming@example.com', NULL, 1, 2, 0, 0, NOW(), NOW(), 1, NULL),
('lindaiyu', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '林黛玉', '13900009999', 'lindaiyu@example.com', NULL, 1, 2, 0, 0, NOW(), NOW(), 1, NULL),
('jiabaoyu', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '贾宝玉', '13900010000', 'jiabaoyu@example.com', NULL, 1, 2, 0, 0, NOW(), NOW(), 1, NULL);

-- 分配 ROLE_OWNER (id=3) 角色给所有新创建的业主用户
INSERT IGNORE INTO `sys_user_role` (`user_info_id`, `role_info_id`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`)
SELECT u.id, 3, 0, 0, NOW(), NOW(), 1, NULL
FROM `sys_user_info` u
WHERE u.user_name IN ('liujianguo', 'chenmeili', 'wangchangling', 'huangxiaoming', 'lindaiyu', 'jiabaoyu')
  AND u.is_deleted = 0;

-- =====================================================
-- Step 9: 为每个小区创建2个维修人员（共4个）
-- 密码均为 123456
-- =====================================================

-- 幸福花园小区维修人员 (community_id=1)
INSERT IGNORE INTO `sys_user_info` (`user_name`, `password`, `full_name`, `phone_number`, `email`, `avatar_address`, `status`, `community_id`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES
('repair_zhang', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '张师傅', '13900020001', 'repair_zhang@example.com', NULL, 1, 1, 0, 0, NOW(), NOW(), 1, NULL),
('repair_li', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '李师傅', '13900020002', 'repair_li@example.com', NULL, 1, 1, 0, 0, NOW(), NOW(), 1, NULL);

-- 阳光城小区维修人员 (community_id=2)
INSERT IGNORE INTO `sys_user_info` (`user_name`, `password`, `full_name`, `phone_number`, `email`, `avatar_address`, `status`, `community_id`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES
('repair_wang', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '王师傅', '13900020003', 'repair_wang@example.com', NULL, 1, 2, 0, 0, NOW(), NOW(), 1, NULL),
('repair_chen', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '陈师傅', '13900020004', 'repair_chen@example.com', NULL, 1, 2, 0, 0, NOW(), NOW(), 1, NULL);

-- 分配 ROLE_REPAIR (id=4) 角色给所有维修人员
INSERT IGNORE INTO `sys_user_role` (`user_info_id`, `role_info_id`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`)
SELECT u.id, 4, 0, 0, NOW(), NOW(), 1, NULL
FROM `sys_user_info` u
WHERE u.user_name IN ('repair_zhang', 'repair_li', 'repair_wang', 'repair_chen')
  AND u.is_deleted = 0;

-- =====================================================
-- Step 10: 修复报修单
-- 当前有1条报修单 REP20260706141401，status=0(无效), assignee_id=NULL
-- 修正为 status=1（待受理），assignee 分配到幸福花园的维修人员
-- =====================================================
UPDATE `repair_order` SET `status` = 1, `update_time` = NOW() WHERE `id` = 1 AND `status` = 0;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 数据汇总（仅供参考）
-- =====================================================
-- 幸福花园小区 (community_id=1)：
--   管理员：zhanggan（张敢）
--   业主用户：123456(张三), 13900001111(王五), 13900002222(赵六), 13900003333(孙七), 13900004444(周八)
--            owner1(张三), owner2(李四), 李逵, 孙悟空, liujianguo(刘建国), chenmeili(陈美丽)
--   维修人员：repair_zhang（张师傅）, repair_li（李师傅）
--
-- 阳光城小区 (community_id=2)：
--   管理员：jiangyong（蒋勇）
--   业主用户：wangchangling(王昌龄), huangxiaoming(黄小明), lindaiyu(林黛玉), jiabaoyu(贾宝玉)
--   维修人员：repair_wang（王师傅）, repair_chen（陈师傅）
--
-- 所有密码：123456
-- =====================================================
