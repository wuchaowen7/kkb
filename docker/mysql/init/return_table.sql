-- ============================================
-- Stock Return Management Tables
-- ============================================

USE smart_inventory;

-- Return Order Table
DROP TABLE IF EXISTS stock_return;
CREATE TABLE stock_return (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    return_no VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(20) DEFAULT 'SALE_RETURN',
    source_order_no VARCHAR(50),
    warehouse_id BIGINT NOT NULL,
    total_amount DECIMAL(12,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'DRAFT',
    remark VARCHAR(500),
    creator_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_return_no (return_no),
    INDEX idx_warehouse_id (warehouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Return Detail Table
DROP TABLE IF EXISTS stock_return_detail;
CREATE TABLE stock_return_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    return_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2),
    reason VARCHAR(200),
    INDEX idx_return_id (return_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert initial return data
INSERT INTO stock_return (return_no, type, source_order_no, warehouse_id, total_amount, status, remark, creator_id) VALUES
('RT202401001', 'SALE_RETURN', 'OUT202401001', 1, 17998.00, 'CONFIRMED', 'Customer return', 1),
('RT202401002', 'PURCHASE_RETURN', 'IN202401001', 2, 8000.00, 'CONFIRMED', 'Purchase return', 1),
('RT202402001', 'SALE_RETURN', 'OUT202402001', 1, 5999.00, 'DRAFT', 'Pending return', 1);

INSERT INTO stock_return_detail (return_id, product_id, quantity, unit_price, reason) VALUES
(1, 1, 2, 8999.00, 'Customer unsatisfied'),
(2, 3, 10, 80.00, 'Quality issue'),
(3, 11, 1, 5999.00, 'Wrong model');

SELECT 'Return tables created successfully!' AS result;