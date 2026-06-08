USE smart_inventory;

-- 更新商品表的生产日期为其库存批次的生产日期
UPDATE base_product
SET production_date = (
    SELECT i.production_date
    FROM stock_inventory i
    WHERE i.product_id = base_product.id AND i.production_date IS NOT NULL
    ORDER BY i.create_time DESC
    LIMIT 1
)
WHERE EXISTS (
    SELECT 1 FROM stock_inventory i 
    WHERE i.product_id = base_product.id AND i.production_date IS NOT NULL
);

SELECT '商品生产日期同步成功' AS result;