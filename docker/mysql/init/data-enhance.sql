-- ============================================
-- Smart Inventory Management System - Data Enhancement Script
-- ============================================

USE smart_inventory;

-- ==================== Add More Products ====================
INSERT INTO base_product (code, name, category_id, spec, unit, cost_price, sale_price, safety_stock, max_stock, expiry_days, barcode, status) VALUES
('P0011', 'iPad Pro 11', 5, '256GB/Wi-Fi', 'unit', 5999.00, 6999.00, 8, 80, 365, '1234567890133', 1),
('P0012', 'AirPods Pro 2', 5, '2nd Gen', 'unit', 1699.00, 1999.00, 20, 100, 365, '1234567890134', 1),
('P0013', 'USB-C Cable', 6, '1m', 'piece', 29.00, 49.00, 100, 500, 365, '1234567890135', 1),
('P0014', 'Power Bank', 5, '20000mAh', 'piece', 129.00, 199.00, 30, 150, 365, '1234567890136', 1),
('P0015', 'Notebook', 4, 'A5/100 pages', 'piece', 5.00, 10.00, 500, 2000, 365, '1234567890137', 1),
('P0016', 'File Folder', 4, 'A4/Blue', 'piece', 3.00, 8.00, 300, 1000, 365, '1234567890138', 1),
('P0017', 'Hand Sanitizer', 7, '300ml', 'bottle', 12.00, 20.00, 150, 600, 365, '1234567890139', 1),
('P0018', 'Towel', 7, 'Cotton/Large', 'piece', 15.00, 25.00, 200, 800, 365, '1234567890140', 1),
('P0019', 'Green Tea', 3, '200g', 'can', 68.00, 98.00, 20, 100, 365, '1234567890141', 1),
('P0020', 'Mixed Nuts', 9, '500g', 'box', 88.00, 128.00, 30, 150, 180, '1234567890142', 1),
('P0021', 'Cola', 10, '330ml*24', 'case', 52.00, 68.00, 100, 500, 365, '1234567890143', 1),
('P0022', 'Coffee', 3, 'Instant/100 sticks', 'box', 58.00, 88.00, 50, 200, 365, '1234567890144', 1),
('P0023', 'Printer', 6, 'Laser/A4', 'unit', 1299.00, 1699.00, 5, 30, 365, '1234567890145', 1),
('P0024', 'Projector', 5, 'Home/1080P', 'unit', 2999.00, 3999.00, 5, 20, 365, '1234567890146', 1),
('P0025', 'Router', 6, 'Wi-Fi 6', 'unit', 299.00, 399.00, 10, 50, 365, '1234567890147', 1);

-- ==================== Add More Inventory ====================
INSERT INTO stock_inventory (product_id, warehouse_id, batch_no, quantity, locked_quantity, production_date, expiry_date) VALUES
(11, 1, 'B20241101', 40, 0, '2024-11-01', '2025-11-01'),
(11, 3, 'B20241102', 25, 3, '2024-11-15', '2025-11-15'),
(12, 1, 'B20241201', 80, 8, '2024-12-01', '2025-12-01'),
(12, 2, 'B20241202', 60, 0, '2024-12-10', '2025-12-10'),
(13, 1, 'B20250101', 300, 20, '2025-01-01', '2026-01-01'),
(14, 2, 'B20250201', 120, 10, '2025-02-01', '2026-02-01'),
(15, 1, 'B20250301', 800, 50, '2025-03-01', '2026-03-01'),
(15, 2, 'B20250302', 600, 0, '2025-03-10', '2026-03-10'),
(16, 3, 'B20250401', 400, 30, '2025-04-01', '2026-04-01'),
(17, 1, 'B20250501', 250, 25, '2025-05-01', '2026-05-01'),
(18, 2, 'B20250601', 350, 0, '2025-06-01', '2026-06-01'),
(19, 1, 'B20250701', 45, 5, '2025-07-01', '2026-07-01'),
(20, 3, 'B20250801', 80, 0, '2025-08-01', '2025-02-01'),
(21, 1, 'B20250901', 200, 20, '2025-09-01', '2026-09-01'),
(22, 2, 'B20251001', 150, 15, '2025-10-01', '2026-10-01'),
(23, 1, 'B20251101', 15, 2, '2025-11-01', '2026-11-01'),
(24, 3, 'B20251201', 8, 1, '2025-12-01', '2026-12-01'),
(25, 2, 'B20260101', 25, 3, '2026-01-01', '2027-01-01');

-- ==================== Add Inbound Orders ====================
INSERT INTO stock_inbound (inbound_no, type, supplier_id, warehouse_id, total_amount, status, remark, creator_id) VALUES
('IN202401001', 'PURCHASE', 1, 1, 40000.00, 'CONFIRMED', 'Purchase iPhone 15 Pro x5', 1),
('IN202401002', 'PURCHASE', 3, 2, 15000.00, 'CONFIRMED', 'Purchase daily supplies', 1),
('IN202402001', 'PURCHASE', 4, 1, 25000.00, 'CONFIRMED', 'Purchase food and beverage', 1),
('IN202403001', 'RETURN', NULL, 1, 5000.00, 'CONFIRMED', 'Customer return', 1),
('IN202403002', 'PURCHASE', 2, 3, 80000.00, 'DRAFT', 'Purchase Huawei devices', 1);

-- Add inbound details
INSERT INTO stock_inbound_detail (inbound_id, product_id, quantity, unit_price, batch_no, production_date, expiry_date) VALUES
(1, 1, 5, 8000.00, 'B20240101', '2024-01-01', '2025-01-01'),
(2, 4, 50, 15.00, 'B20240401', '2024-04-01', '2025-04-01'),
(2, 5, 100, 8.00, 'B20240501', '2024-05-01', '2025-05-01'),
(3, 6, 200, 12.00, 'B20240601', '2024-06-01', '2024-12-01'),
(3, 7, 100, 18.00, 'B20240701', '2024-07-01', '2025-07-01'),
(4, 1, 1, 8999.00, 'B20240101', '2024-01-01', '2025-01-01'),
(5, 11, 10, 5999.00, 'B20241101', '2024-11-01', '2025-11-01');

-- ==================== Add Outbound Orders ====================
INSERT INTO stock_outbound (outbound_no, type, department, warehouse_id, status, audit_opinion, remark, creator_id, auditor_id, audit_time) VALUES
('OUT202401001', 'SALE', NULL, 1, 'AUDITED', 'Approved', 'Sales outbound', 1, 1, '2024-01-15 10:30:00'),
('OUT202401002', 'USE', 'R&D', 1, 'AUDITED', 'Approved', 'Office supplies', 1, 1, '2024-01-20 14:00:00'),
('OUT202402001', 'SALE', NULL, 2, 'PENDING_AUDIT', NULL, 'Pending approval', 1, NULL, NULL),
('OUT202402002', 'USE', 'Marketing', 1, 'REJECTED', 'Insufficient stock', 'Rejected request', 1, 1, '2024-02-10 09:00:00'),
('OUT202403001', 'SALE', NULL, 3, 'DRAFT', NULL, 'Draft order', 1, NULL, NULL);

-- Add outbound details
INSERT INTO stock_outbound_detail (outbound_id, product_id, quantity, inventory_id) VALUES
(1, 1, 2, 1),
(1, 12, 5, 13),
(2, 8, 10, 14),
(2, 9, 20, 15),
(3, 2, 1, 3),
(4, 24, 2, 18);

-- ==================== Add Transfer Orders ====================
INSERT INTO stock_transfer (transfer_no, from_warehouse_id, to_warehouse_id, product_id, quantity, status, remark, creator_id) VALUES
('TF202401001', 1, 2, 3, 20, 'CONFIRMED', 'Transfer BJ to SH', 1),
('TF202402001', 2, 3, 6, 50, 'CONFIRMED', 'Transfer SH to GZ', 1),
('TF202403001', 1, 3, 7, 100, 'PENDING', 'Pending transfer', 1),
('TF202403002', 3, 1, 12, 10, 'PENDING', 'Transfer GZ to BJ', 1);

-- ==================== Add Check Orders ====================
INSERT INTO stock_check (check_no, warehouse_id, total_count, diff_count, status, remark, creator_id) VALUES
('CK202401001', 1, 10, 2, 'CONFIRMED', 'Annual inventory check', 1),
('CK202402001', 2, 8, 0, 'CONFIRMED', 'Quarterly check', 1),
('CK202403001', 3, 6, 1, 'CHECKING', 'Monthly checking', 1);

-- ==================== Add More Alerts ====================
INSERT INTO intel_alert (product_id, warehouse_id, alert_type, alert_level, message, status, handler_id, handle_time) VALUES
(12, 1, 'SHORTAGE', 'WARN', 'AirPods Pro low stock: 80 below safety 20', 0, NULL, NULL),
(14, 2, 'SHORTAGE', 'INFO', 'Power Bank low stock', 0, NULL, NULL),
(17, 1, 'OVERSTOCK', 'WARN', 'Hand Sanitizer overstock: 250 above max 200', 0, NULL, NULL),
(20, 3, 'EXPIRY', 'ERROR', 'Mixed Nuts expiry warning: 15 days remaining', 0, NULL, NULL),
(21, 1, 'OVERSTOCK', 'INFO', 'Cola high stock', 1, 1, '2024-12-01 10:00:00'),
(24, 3, 'SHORTAGE', 'WARN', 'Projector low stock: 8 below safety 5', 0, NULL, NULL),
(6, 1, 'EXPIRY', 'ERROR', 'Instant Noodles expiry: 30 days', 0, NULL, NULL),
(7, 1, 'SLOW_MOVING', 'INFO', 'Mineral Water slow sales', 0, NULL, NULL),
(15, 1, 'OVERSTOCK', 'WARN', 'Notebook overstock: 800 above max 500', 0, NULL, NULL),
(18, 2, 'SLOW_MOVING', 'INFO', 'Towel slow sales', 1, 1, '2024-11-15 14:30:00');

-- ==================== Add More Users ====================
INSERT INTO sys_user (username, password, real_name, phone, status) VALUES
('zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHs', 'Zhang San', '13800000101', 1),
('lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHs', 'Li Si', '13800000102', 1),
('wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHs', 'Wang Wu', '13800000103', 1),
('zhaoliu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHs', 'Zhao Liu', '13800000104', 1);

-- User role assignments
INSERT INTO sys_user_role (user_id, role_id) VALUES
(2, 2),
(3, 3),
(4, 4),
(5, 5);

-- ==================== Add deleted column to product table ====================
ALTER TABLE base_product ADD COLUMN deleted TINYINT DEFAULT 0;

SELECT 'Data enhancement completed!' AS result;