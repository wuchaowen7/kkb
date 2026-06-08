USE smart_inventory;

-- 更新库存数据，设置合理的生产日期和过期日期
-- 过期日期 = 生产日期 + 商品保质期天数

UPDATE stock_inventory i
JOIN base_product p ON i.product_id = p.id
SET 
    i.production_date = DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 180) + 30 DAY),
    i.expiry_date = DATE_ADD(
        DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 180) + 30 DAY), 
        INTERVAL COALESCE(p.expiry_days, 365) DAY
    )
WHERE i.id > 0;

SELECT '库存日期更新成功' AS result;