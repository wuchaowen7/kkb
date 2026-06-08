USE smart_inventory;

-- 为商品表添加生产日期字段
ALTER TABLE base_product ADD COLUMN production_date DATE COMMENT '生产日期' AFTER expiry_days;

-- 更新数据库初始化脚本
-- ALTER TABLE base_product MODIFY COLUMN expiry_days INT COMMENT '保质期天数';

SELECT '字段添加成功' AS result;