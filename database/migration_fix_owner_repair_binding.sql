-- =====================================================
-- 迁移脚本：修复业主用户绑定小区、创建维修人员、清理跨小区数据
-- 日期：2026-07-06
-- 说明：
--   1. 将现有的业主用户绑定到正确的小区
--      - testowner (id=2) -> 幸福花园小区 (community_id=1)，手机号 13800138000
--      - 123456/张三 (id=3) -> 幸福花园小区 (community_id=1)，默认
--   2. 创建每个小区的业主用户，确保每个小区都有业主用户
--   3. 创建业主信息(owner_info)数据，绑定到对应房屋
--   4. 为每个小区创建2个维修人员（共4个）
--   5. 如果存在报修单数据，修正跨小区的报修单关联
-- =====================================================

USE spms;

SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- Step 1: 更新现有业主用户的小区绑定
-- =====================================================

-- testowner 用户绑定到幸福花园小区 (community_id=1)
UPDATE `sys_user_info` SET `community_id` = 1, `update_time` = NOW() WHERE `id` = 2;

-- 张三(123456) 用户绑定到幸福花园小区 (community_id=1)
UPDATE `sys_user_info` SET `community_id` = 1, `update_time` = NOW() WHERE `id` = 3;

-- =====================================================
-- Step 2: 创建业主信息(owner_info)数据
-- 为两个小区的房屋创建对应的业主记录
-- =====================================================

-- 幸福花园小区 (community_id=1) 的业主
-- A栋101 -> 王五 (phone: 13900001111)
INSERT IGNORE INTO `owner_info` (`id`, `owner_name`, `owner_phone`, `id_card`, `gender`, `email`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES (1, '王五', '13900001111', '110101199001011234', 1, 'wangwu@example.com', 1, 0, 0, NOW(), NOW(), 1);

-- A栋102 -> 赵六 (phone: 13900002222)
INSERT IGNORE INTO `owner_info` (`id`, `owner_name`, `owner_phone`, `id_card`, `gender`, `email`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES (2, '赵六', '13900002222', '110101199002022345', 0, 'zhaoliu@example.com', 1, 0, 0, NOW(), NOW(), 1);

-- C栋101 -> 孙七 (phone: 13900003333)
INSERT IGNORE INTO `owner_info` (`id`, `owner_name`, `owner_phone`, `id_card`, `gender`, `email`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES (3, '孙七', '13900003333', '110101199003033456', 1, 'sunqi@example.com', 1, 0, 0, NOW(), NOW(), 1);

-- C栋102 -> 周八 (phone: 13900004444)
INSERT IGNORE INTO `owner_info` (`id`, `owner_name`, `owner_phone`, `id_card`, `gender`, `email`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES (4, '周八', '13900004444', '110101199004044567', 0, 'zhouba@example.com', 1, 0, 0, NOW(), NOW(), 1);

-- B栋101 -> 刘建国 (phone: 13900005555)
INSERT IGNORE INTO `owner_info` (`id`, `owner_name`, `owner_phone`, `id_card`, `gender`, `email`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES (5, '刘建国', '13900005555', '110101198505055678', 1, 'liujianguo@example.com', 1, 0, 0, NOW(), NOW(), 1);

-- B栋201 -> 陈美丽 (phone: 13900006666)
INSERT IGNORE INTO `owner_info` (`id`, `owner_name`, `owner_phone`, `id_card`, `gender`, `email`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES (6, '陈美丽', '13900006666', '110101198606066789', 0, 'chenmeili@example.com', 1, 0, 0, NOW(), NOW(), 1);

-- 阳光城小区 (community_id=2) 的业主
-- 1号楼101 -> 王昌龄 (phone: 13900007777)
INSERT IGNORE INTO `owner_info` (`id`, `owner_name`, `owner_phone`, `id_card`, `gender`, `email`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES (7, '王昌龄', '13900007777', '310101198707077890', 1, 'wangchangling@example.com', 1, 0, 0, NOW(), NOW(), 1);

-- 1号楼201 -> 黄小明 (phone: 13900008888)
INSERT IGNORE INTO `owner_info` (`id`, `owner_name`, `owner_phone`, `id_card`, `gender`, `email`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES (8, '黄小明', '13900008888', '310101198808088901', 1, 'huangxiaoming@example.com', 1, 0, 0, NOW(), NOW(), 1);

-- 2号楼101 -> 林黛玉 (phone: 13900009999)
INSERT IGNORE INTO `owner_info` (`id`, `owner_name`, `owner_phone`, `id_card`, `gender`, `email`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES (9, '林黛玉', '13900009999', '310101199009099012', 0, 'lindaiyu@example.com', 1, 0, 0, NOW(), NOW(), 1);

-- 2号楼201 -> 贾宝玉 (phone: 13900010000)
INSERT IGNORE INTO `owner_info` (`id`, `owner_name`, `owner_phone`, `id_card`, `gender`, `email`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES (10, '贾宝玉', '13900010000', '310101199110100123', 1, 'jiabaoyu@example.com', 1, 0, 0, NOW(), NOW(), 1);

-- =====================================================
-- Step 3: 创建业主-房屋关联关系 (owner_house_rel)
-- =====================================================

-- 幸福花园小区 (community_id=1) 的业主房屋关联
-- 王五 -> A栋101 (house_id=1)
INSERT IGNORE INTO `owner_house_rel` (`owner_info_id`, `house_info_id`, `relation_type`, `is_primary`, `move_in_date`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES (1, 1, 1, 1, '2024-01-01', 0, 0, NOW(), NOW(), 1);

-- 赵六 -> A栋102 (house_id=2)
INSERT IGNORE INTO `owner_house_rel` (`owner_info_id`, `house_info_id`, `relation_type`, `is_primary`, `move_in_date`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES (2, 2, 1, 1, '2024-02-01', 0, 0, NOW(), NOW(), 1);

-- 孙七 -> C栋101 (house_id=5)
INSERT IGNORE INTO `owner_house_rel` (`owner_info_id`, `house_info_id`, `relation_type`, `is_primary`, `move_in_date`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES (3, 5, 1, 1, '2024-03-01', 0, 0, NOW(), NOW(), 1);

-- 周八 -> C栋102 (house_id=6)
INSERT IGNORE INTO `owner_house_rel` (`owner_info_id`, `house_info_id`, `relation_type`, `is_primary`, `move_in_date`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
VALUES (4, 6, 1, 1, '2024-04-01', 0, 0, NOW(), NOW(), 1);

-- 刘建国 -> B栋101 (house_id: 在migration_add_test_buildings_houses中B栋101对应)
INSERT IGNORE INTO `owner_house_rel` (`owner_info_id`, `house_info_id`, `relation_type`, `is_primary`, `move_in_date`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
SELECT 5, id, 1, 1, '2024-05-01', 0, 0, NOW(), NOW(), 1 FROM house_info WHERE building_id = 2 AND house_number = '101' AND is_deleted = 0 LIMIT 1;

-- 陈美丽 -> B栋201 (house_id: B栋201)
INSERT IGNORE INTO `owner_house_rel` (`owner_info_id`, `house_info_id`, `relation_type`, `is_primary`, `move_in_date`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
SELECT 6, id, 1, 1, '2024-06-01', 0, 0, NOW(), NOW(), 1 FROM house_info WHERE building_id = 2 AND house_number = '201' AND is_deleted = 0 LIMIT 1;

-- 阳光城小区 (community_id=2) 的业主房屋关联
-- 王昌龄 -> 1号楼101 (house_id: 1号楼101)
INSERT IGNORE INTO `owner_house_rel` (`owner_info_id`, `house_info_id`, `relation_type`, `is_primary`, `move_in_date`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
SELECT 7, id, 1, 1, '2024-07-01', 0, 0, NOW(), NOW(), 1 FROM house_info WHERE building_id = 4 AND house_number = '101' AND is_deleted = 0 LIMIT 1;

-- 黄小明 -> 1号楼201 (house_id: 1号楼201)
INSERT IGNORE INTO `owner_house_rel` (`owner_info_id`, `house_info_id`, `relation_type`, `is_primary`, `move_in_date`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
SELECT 8, id, 1, 1, '2024-08-01', 0, 0, NOW(), NOW(), 1 FROM house_info WHERE building_id = 4 AND house_number = '201' AND is_deleted = 0 LIMIT 1;

-- 林黛玉 -> 2号楼101 (house_id: 2号楼101)
INSERT IGNORE INTO `owner_house_rel` (`owner_info_id`, `house_info_id`, `relation_type`, `is_primary`, `move_in_date`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
SELECT 9, id, 1, 1, '2024-09-01', 0, 0, NOW(), NOW(), 1 FROM house_info WHERE building_id = 5 AND house_number = '101' AND is_deleted = 0 LIMIT 1;

-- 贾宝玉 -> 2号楼201 (house_id: 2号楼201)
INSERT IGNORE INTO `owner_house_rel` (`owner_info_id`, `house_info_id`, `relation_type`, `is_primary`, `move_in_date`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`)
SELECT 10, id, 1, 1, '2024-10-01', 0, 0, NOW(), NOW(), 1 FROM house_info WHERE building_id = 5 AND house_number = '201' AND is_deleted = 0 LIMIT 1;

-- =====================================================
-- Step 4: 更新 house_info 的 owner_name/owner_phone/status
-- 将已有业主的房屋标记为已入住
-- =====================================================

-- 幸福花园小区
UPDATE `house_info` SET `owner_name` = '王五', `owner_phone` = '13900001111', `status` = 1, `update_time` = NOW() WHERE `id` = 1;
UPDATE `house_info` SET `owner_name` = '赵六', `owner_phone` = '13900002222', `status` = 1, `update_time` = NOW() WHERE `id` = 2;
UPDATE `house_info` SET `owner_name` = '孙七', `owner_phone` = '13900003333', `status` = 1, `update_time` = NOW() WHERE `id` = 5;
UPDATE `house_info` SET `owner_name` = '周八', `owner_phone` = '13900004444', `status` = 1, `update_time` = NOW() WHERE `id` = 6;

-- 幸福花园 B栋101, B栋201
UPDATE `house_info` SET `owner_name` = '刘建国', `owner_phone` = '13900005555', `status` = 1, `update_time` = NOW() WHERE `building_id` = 2 AND `house_number` = '101' AND `is_deleted` = 0;
UPDATE `house_info` SET `owner_name` = '陈美丽', `owner_phone` = '13900006666', `status` = 1, `update_time` = NOW() WHERE `building_id` = 2 AND `house_number` = '201' AND `is_deleted` = 0;

-- 阳光城小区
UPDATE `house_info` SET `owner_name` = '王昌龄', `owner_phone` = '13900007777', `status` = 1, `update_time` = NOW() WHERE `building_id` = 4 AND `house_number` = '101' AND `is_deleted` = 0;
UPDATE `house_info` SET `owner_name` = '黄小明', `owner_phone` = '13900008888', `status` = 1, `update_time` = NOW() WHERE `building_id` = 4 AND `house_number` = '201' AND `is_deleted` = 0;
UPDATE `house_info` SET `owner_name` = '林黛玉', `owner_phone` = '13900009999', `status` = 1, `update_time` = NOW() WHERE `building_id` = 5 AND `house_number` = '101' AND `is_deleted` = 0;
UPDATE `house_info` SET `owner_name` = '贾宝玉', `owner_phone` = '13900010000', `status` = 1, `update_time` = NOW() WHERE `building_id` = 5 AND `house_number` = '201' AND `is_deleted` = 0;

-- =====================================================
-- Step 5: 为每个小区创建业主系统用户（用于登录）
-- 密码均为 123456（BCrypt加密）
-- =====================================================

-- 幸福花园小区业主用户 (community_id=1)
-- 王五 (已有 testowner 用户，手机号不同，需要新创建)
INSERT IGNORE INTO `sys_user_info` (`user_name`, `password`, `full_name`, `phone_number`, `email`, `avatar_address`, `status`, `community_id`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES
('wangwu', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '王五', '13900001111', 'wangwu@example.com', NULL, 1, 1, 0, 0, NOW(), NOW(), 1, NULL),
('zhaoliu', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '赵六', '13900002222', 'zhaoliu@example.com', NULL, 1, 1, 0, 0, NOW(), NOW(), 1, NULL),
('sunqi', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '孙七', '13900003333', 'sunqi@example.com', NULL, 1, 1, 0, 0, NOW(), NOW(), 1, NULL),
('zhouba', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '周八', '13900004444', 'zhouba@example.com', NULL, 1, 1, 0, 0, NOW(), NOW(), 1, NULL),
('liujianguo', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '刘建国', '13900005555', 'liujianguo@example.com', NULL, 1, 1, 0, 0, NOW(), NOW(), 1, NULL),
('chenmeili', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '陈美丽', '13900006666', 'chenmeili@example.com', NULL, 1, 1, 0, 0, NOW(), NOW(), 1, NULL);

-- 阳光城小区业主用户 (community_id=2)
INSERT IGNORE INTO `sys_user_info` (`user_name`, `password`, `full_name`, `phone_number`, `email`, `avatar_address`, `status`, `community_id`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES
('wangchangling', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '王昌龄', '13900007777', 'wangchangling@example.com', NULL, 1, 2, 0, 0, NOW(), NOW(), 1, NULL),
('huangxiaoming', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '黄小明', '13900008888', 'huangxiaoming@example.com', NULL, 1, 2, 0, 0, NOW(), NOW(), 1, NULL),
('lindaiyu', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '林黛玉', '13900009999', 'lindaiyu@example.com', NULL, 1, 2, 0, 0, NOW(), NOW(), 1, NULL),
('jiabaoyu', '$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC', '贾宝玉', '13900010000', 'jiabaoyu@example.com', NULL, 1, 2, 0, 0, NOW(), NOW(), 1, NULL);

-- 分配 ROLE_OWNER (id=3) 角色给所有业主用户
INSERT IGNORE INTO `sys_user_role` (`user_info_id`, `role_info_id`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`)
SELECT u.id, 3, 0, 0, NOW(), NOW(), 1, NULL
FROM `sys_user_info` u
WHERE u.user_name IN ('wangwu', 'zhaoliu', 'sunqi', 'zhouba', 'liujianguo', 'chenmeili', 'wangchangling', 'huangxiaoming', 'lindaiyu', 'jiabaoyu')
  AND u.is_deleted = 0;

-- =====================================================
-- Step 6: 为每个小区创建2个维修人员（共4个）
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
-- Step 7: 如果存在报修单数据，清理跨小区的报修单
-- 将 owner_id 为 NULL 或不存在的报修单修正
-- =====================================================

-- 如果 repair_order 中存在数据，确保 owner_id 对应正确的 owner_info
-- 注意：如果之前通过前端创建了报修单，其 owner_id 需要指向 owner_info 而非 sys_user_info

-- =====================================================
-- Step 8: 更新房屋状态，将未分配业主的房屋设为空置
-- =====================================================

-- 将没有业主信息的房屋状态设为空置(0)
UPDATE `house_info` 
SET `status` = 0, `owner_name` = NULL, `owner_phone` = NULL, `update_time` = NOW()
WHERE `owner_name` IS NULL AND `is_deleted` = 0;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 数据汇总（仅供参考）
-- =====================================================
-- 幸福花园小区 (community_id=1)：
--   业主用户：testowner, 张三(123456), wangwu, zhaoliu, sunqi, zhouba, liujianguo, chenmeili
--   管理员：zhanggan（张敢）
--   维修人员：repair_zhang（张师傅）, repair_li（李师傅）
--
-- 阳光城小区 (community_id=2)：
--   业主用户：wangchangling, huangxiaoming, lindaiyu, jiabaoyu
--   管理员：jiangyong（蒋勇）
--   维修人员：repair_wang（王师傅）, repair_chen（陈师傅）
--
-- 所有密码：123456
-- =====================================================
