-- ============================================
-- 迁移脚本：house_info 表添加 has_parking 字段
-- 执行前请确认数据库名称正确
-- ============================================

USE spms;

-- 添加 has_parking 字段（如果不存在）
-- 0-无车位  1-有车位
ALTER TABLE house_info
    ADD COLUMN has_parking TINYINT DEFAULT 0 NULL COMMENT '是否有车位: 0-无 1-有'
    AFTER owner_phone;

-- 验证字段是否添加成功
SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'spms'
  AND TABLE_NAME = 'house_info'
  AND COLUMN_NAME = 'has_parking';
