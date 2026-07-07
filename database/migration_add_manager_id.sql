-- 为 community_info 表添加 manager_id 字段
-- 用于记录小区负责人的用户ID（物业管理员）

ALTER TABLE community_info
    ADD COLUMN manager_id bigint NULL COMMENT '负责人用户ID' AFTER property_company;
