-- ============================================
-- 智能库存管理系统 - 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS smart_inventory DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_inventory;

-- ==================== 系统管理模块 ====================

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(200) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    dept_id BIGINT DEFAULT 0 COMMENT '部门ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 菜单/权限表
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '菜单ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    menu_type CHAR(1) DEFAULT 'C' COMMENT '类型: M-目录 C-菜单 F-按钮',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(200) COMMENT '组件路径',
    perms VARCHAR(200) COMMENT '权限标识',
    icon VARCHAR(100) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    visible TINYINT DEFAULT 1 COMMENT '是否可见',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单/权限表';

-- 用户-角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

-- 角色-菜单关联表
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    INDEX idx_role_id (role_id),
    INDEX idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-菜单关联表';

-- ==================== 基础信息管理模块 ====================

-- 商品分类表
DROP TABLE IF EXISTS base_category;
CREATE TABLE base_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
DROP TABLE IF EXISTS base_product;
CREATE TABLE base_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '商品编码',
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    category_id BIGINT COMMENT '分类ID',
    spec VARCHAR(100) COMMENT '规格',
    unit VARCHAR(20) COMMENT '单位',
    cost_price DECIMAL(10,2) COMMENT '成本价',
    sale_price DECIMAL(10,2) COMMENT '售价',
    safety_stock INT DEFAULT 0 COMMENT '安全库存阈值',
    max_stock INT DEFAULT 0 COMMENT '最大库存阈值',
    expiry_days INT COMMENT '保质期天数',
    production_date DATE COMMENT '生产日期',
    barcode VARCHAR(100) COMMENT '条码',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 仓库表
DROP TABLE IF EXISTS base_warehouse;
CREATE TABLE base_warehouse (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '仓库ID',
    name VARCHAR(100) NOT NULL COMMENT '仓库名称',
    address VARCHAR(200) COMMENT '地址',
    manager VARCHAR(50) COMMENT '负责人',
    phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

-- 供应商表
DROP TABLE IF EXISTS base_supplier;
CREATE TABLE base_supplier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '供应商ID',
    name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    contact_name VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(200) COMMENT '地址',
    remark VARCHAR(500) COMMENT '备注',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- ==================== 库存业务模块 ====================

-- 库存表
DROP TABLE IF EXISTS stock_inventory;
CREATE TABLE stock_inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '库存ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    zone_id BIGINT DEFAULT 0 COMMENT '分区/库位ID',
    batch_no VARCHAR(50) COMMENT '批次号',
    quantity INT DEFAULT 0 COMMENT '当前库存数量',
    locked_quantity INT DEFAULT 0 COMMENT '锁定库存数量',
    production_date DATE COMMENT '生产日期',
    expiry_date DATE COMMENT '过期日期',
    version INT DEFAULT 1 COMMENT '乐观锁版本号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_product (product_id),
    INDEX idx_warehouse (warehouse_id),
    INDEX idx_batch (batch_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 入库单
DROP TABLE IF EXISTS stock_inbound;
CREATE TABLE stock_inbound (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '入库单ID',
    inbound_no VARCHAR(50) NOT NULL UNIQUE COMMENT '入库单号',
    type VARCHAR(20) DEFAULT 'PURCHASE' COMMENT '类型: PURCHASE-采购入库 RETURN-退货入库',
    supplier_id BIGINT COMMENT '供应商ID',
    warehouse_id BIGINT NOT NULL COMMENT '目标仓库ID',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿 CONFIRMED-已确认',
    remark VARCHAR(500) COMMENT '备注',
    creator_id BIGINT COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单';

-- 入库明细
DROP TABLE IF EXISTS stock_inbound_detail;
CREATE TABLE stock_inbound_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明细ID',
    inbound_id BIGINT NOT NULL COMMENT '入库单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '入库数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    batch_no VARCHAR(50) COMMENT '批次号',
    production_date DATE COMMENT '生产日期',
    expiry_date DATE COMMENT '过期日期',
    INDEX idx_inbound_id (inbound_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库明细';

-- 出库单
DROP TABLE IF EXISTS stock_outbound;
CREATE TABLE stock_outbound (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '出库单ID',
    outbound_no VARCHAR(50) NOT NULL UNIQUE COMMENT '出库单号',
    type VARCHAR(20) DEFAULT 'SALE' COMMENT '类型: SALE-销售出库 USE-领用出库',
    department VARCHAR(100) COMMENT '领用部门',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    status VARCHAR(20) DEFAULT 'PENDING_AUDIT' COMMENT '状态: DRAFT/PENDING_AUDIT/AUDITED/REJECTED',
    audit_opinion VARCHAR(200) COMMENT '审核意见',
    remark VARCHAR(500) COMMENT '备注',
    creator_id BIGINT COMMENT '创建人ID',
    auditor_id BIGINT COMMENT '审核人ID',
    audit_time DATETIME COMMENT '审核时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单';

-- 出库明细
DROP TABLE IF EXISTS stock_outbound_detail;
CREATE TABLE stock_outbound_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明细ID',
    outbound_id BIGINT NOT NULL COMMENT '出库单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '出库数量',
    inventory_id BIGINT COMMENT '库存批次ID',
    INDEX idx_outbound_id (outbound_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库明细';

-- 调拨单
DROP TABLE IF EXISTS stock_transfer;
CREATE TABLE stock_transfer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '调拨单ID',
    transfer_no VARCHAR(50) NOT NULL UNIQUE COMMENT '调拨单号',
    from_warehouse_id BIGINT NOT NULL COMMENT '源仓库ID',
    to_warehouse_id BIGINT NOT NULL COMMENT '目标仓库ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '调拨数量',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING-待确认 CONFIRMED-已确认',
    remark VARCHAR(500) COMMENT '备注',
    creator_id BIGINT COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调拨单';

-- 盘点单
DROP TABLE IF EXISTS stock_check;
CREATE TABLE stock_check (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '盘点单ID',
    check_no VARCHAR(50) NOT NULL UNIQUE COMMENT '盘点单号',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    total_count INT DEFAULT 0 COMMENT '商品种类数',
    diff_count INT DEFAULT 0 COMMENT '差异种类数',
    status VARCHAR(20) DEFAULT 'CHECKING' COMMENT '状态: CHECKING-盘点中 CONFIRMED-已确认',
    remark VARCHAR(500) COMMENT '备注',
    creator_id BIGINT COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点单';

-- ==================== 智能模块 ====================

-- 预警记录表
DROP TABLE IF EXISTS intel_alert;
CREATE TABLE intel_alert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '预警ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    warehouse_id BIGINT COMMENT '仓库ID',
    alert_type VARCHAR(20) NOT NULL COMMENT '类型: SHORTAGE-缺货 OVERSTOCK-积压 EXPIRY-临期 SLOW_MOVING-呆滞',
    alert_level VARCHAR(10) DEFAULT 'WARN' COMMENT '级别: INFO/WARN/ERROR',
    message VARCHAR(500) COMMENT '预警信息',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-未处理 1-已处理',
    handler_id BIGINT COMMENT '处理人ID',
    handle_time DATETIME COMMENT '处理时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product (product_id),
    INDEX idx_type (alert_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警记录表';

-- ==================== 日志模块 ====================

-- 操作日志表
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    title VARCHAR(100) COMMENT '操作标题',
    business_type VARCHAR(50) COMMENT '业务类型',
    method VARCHAR(200) COMMENT '方法名',
    request_method VARCHAR(10) COMMENT '请求方式',
    oper_name VARCHAR(50) COMMENT '操作人',
    oper_ip VARCHAR(50) COMMENT '操作IP',
    oper_url VARCHAR(200) COMMENT '请求URL',
    oper_param TEXT COMMENT '请求参数',
    json_result TEXT COMMENT '返回结果',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-失败 1-成功',
    error_msg TEXT COMMENT '错误信息',
    cost_time BIGINT COMMENT '耗时(ms)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 库存流水表
DROP TABLE IF EXISTS stock_inventory_log;
CREATE TABLE stock_inventory_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '流水ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    biz_type VARCHAR(20) NOT NULL COMMENT '业务类型: INBOUND/OUTBOUND/TRANSFER/CHECK',
    biz_no VARCHAR(50) COMMENT '业务单号',
    change_qty INT NOT NULL COMMENT '变动数量（+正/-负）',
    before_qty INT COMMENT '变动前数量',
    after_qty INT COMMENT '变动后数量',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product (product_id),
    INDEX idx_biz_type (biz_type),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水表';

-- ==================== 初始化数据 ====================

-- 默认管理员用户 (密码: admin123)
INSERT INTO sys_user (username, password, real_name, phone, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHs', '超级管理员', '13800000000', 1);

-- 默认角色
INSERT INTO sys_role (role_name, role_code, description) VALUES
('超级管理员', 'SUPER_ADMIN', '拥有所有权限'),
('仓库管理员', 'WAREHOUSE_ADMIN', '仓库管理'),
('采购员', 'PURCHASER', '采购管理'),
('操作员', 'OPERATOR', '基本操作'),
('财务', 'FINANCE', '财务查看');

-- 默认菜单
INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, perms, icon, sort) VALUES
(0, '首页', 'C', '/dashboard', 'dashboard/index', '', 'HomeFilled', 1),
(0, '基础信息', 'M', '/base', '', '', 'Box', 2),
(2, '商品管理', 'C', '/base/products', 'base/product/index', 'base:product:list', '', 1),
(2, '仓库管理', 'C', '/base/warehouses', 'base/warehouse/index', 'base:warehouse:list', '', 2),
(2, '供应商管理', 'C', '/base/suppliers', 'base/supplier/index', 'base:supplier:list', '', 3),
(0, '库存业务', 'M', '/stock', '', '', 'ShoppingBag', 3),
(6, '库存查询', 'C', '/stock/inventory', 'stock/inventory/index', 'stock:inventory:list', '', 1),
(6, '入库管理', 'C', '/stock/inbound', 'stock/inbound/index', 'stock:inbound:list', '', 2),
(6, '出库管理', 'C', '/stock/outbound', 'stock/outbound/index', 'stock:outbound:list', '', 3),
(6, '库存调拨', 'C', '/stock/transfer', 'stock/transfer/index', 'stock:transfer:list', '', 4),
(6, '库存盘点', 'C', '/stock/check', 'stock/check/index', 'stock:check:list', '', 5),
(6, '退库管理', 'C', '/stock/return', 'stock/return/index', 'stock:return:list', '', 6),
(0, '智能模块', 'M', '/intelligence', '', '', 'Cpu', 4),
(12, '预警列表', 'C', '/intelligence/alerts', 'intelligence/alert/index', 'intelligence:alert:list', '', 1),
(12, '销量预测', 'C', '/intelligence/predict', 'intelligence/predict/index', 'intelligence:predict:view', '', 2),
(12, '智能补货', 'C', '/intelligence/replenish', 'intelligence/replenish/index', 'intelligence/replenish:view', '', 3),
(12, '智能分析', 'C', '/intelligence/analysis', 'intelligence/analysis/index', 'intelligence/analysis:view', '', 4),
(0, '报表', 'M', '/report', '', '', 'PieChart', 5),
(17, '总览看板', 'C', '/report/dashboard', 'report/dashboard/index', 'report:dashboard:view', '', 1),
(17, '进销存报表', 'C', '/report/in-out-stock', 'report/in-out-stock/index', 'report:inout:view', '', 2),
(17, '库存明细', 'C', '/report/inventory-detail', 'report/detail/index', 'report:detail:view', '', 3),
(0, '系统管理', 'M', '/system', '', '', 'Settings', 6),
(21, '用户管理', 'C', '/system/user', 'system/user/index', 'system:user:list', '', 1),
(21, '角色管理', 'C', '/system/role', 'system/role/index', 'system:role:list', '', 2),
(21, '菜单管理', 'C', '/system/menu', 'system/menu/index', 'system:menu:list', '', 3),
(21, '操作日志', 'C', '/system/log', 'system/log/index', 'system:log:list', '', 4),
(21, '数据备份', 'C', '/system/backup', 'system/backup/index', 'system/backup:manage', '', 5),
(21, '系统配置', 'C', '/system/config', 'system/config/index', 'system:config:manage', '', 6);

-- 超级管理员角色分配全部菜单
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;

-- 初始化商品分类数据
INSERT INTO base_category (parent_id, name, sort, status) VALUES
(0, '电子产品', 1, 1),
(0, '日用品', 2, 1),
(0, '食品饮料', 3, 1),
(0, '办公用品', 4, 1),
(1, '手机数码', 1, 1),
(1, '电脑配件', 2, 1),
(2, '清洁用品', 1, 1),
(2, '厨房用品', 2, 1),
(3, '零食', 1, 1),
(3, '饮料', 2, 1);

-- 初始化商品数据
INSERT INTO base_product (code, name, category_id, spec, unit, cost_price, sale_price, safety_stock, max_stock, expiry_days, barcode, status) VALUES
('P0001', 'iPhone 15 Pro', 5, '256GB', '台', 7999.00, 8999.00, 10, 100, 365, '1234567890123', 1),
('P0002', 'MacBook Pro 14', 6, 'M3 Pro/18GB/512GB', '台', 14999.00, 16999.00, 5, 50, 365, '1234567890124', 1),
('P0003', '无线鼠标', 6, '蓝牙/2.4G', '个', 59.00, 89.00, 50, 200, 365, '1234567890125', 1),
('P0004', '洗衣液', 7, '500ml', '瓶', 15.00, 25.00, 100, 500, 365, '1234567890126', 1),
('P0005', '洗洁精', 8, '500ml', '瓶', 8.00, 15.00, 200, 800, 365, '1234567890127', 1),
('P0006', '方便面', 9, '5连包', '组', 12.00, 18.00, 500, 2000, 180, '1234567890128', 1),
('P0007', '矿泉水', 10, '550ml*12', '箱', 18.00, 28.00, 200, 1000, 365, '1234567890129', 1),
('P0008', '打印纸', 4, 'A4/80g/500张', '包', 25.00, 35.00, 100, 500, 365, '1234567890130', 1),
('P0009', '中性笔', 4, '0.5mm黑色', '盒', 10.00, 18.00, 200, 1000, 365, '1234567890131', 1),
('P0010', '耳机', 5, '无线蓝牙', '副', 199.00, 299.00, 30, 150, 365, '1234567890132', 1);

-- 初始化仓库数据
INSERT INTO base_warehouse (name, address, manager, phone, status) VALUES
('北京总仓库', '北京市朝阳区科技园A座', '张三', '13800000001', 1),
('上海分仓库', '上海市浦东新区工业园B区', '李四', '13800000002', 1),
('广州分仓库', '广州市天河区科技城C栋', '王五', '13800000003', 1);

-- 初始化供应商数据
INSERT INTO base_supplier (name, contact_name, phone, address, remark, status) VALUES
('苹果中国有限公司', '赵六', '13900000001', '上海市浦东新区', '官方授权', 1),
('华为技术有限公司', '孙七', '13900000002', '深圳市南山区', '战略合作伙伴', 1),
('宝洁中国', '周八', '13900000003', '广州市天河区', '日用品供应商', 1),
('康师傅集团', '吴九', '13900000004', '天津市经济开发区', '食品饮料供应商', 1);

-- 初始化库存数据
INSERT INTO stock_inventory (product_id, warehouse_id, batch_no, quantity, locked_quantity, production_date, expiry_date) VALUES
(1, 1, 'B20240101', 50, 5, '2024-01-01', '2025-01-01'),
(1, 2, 'B20240102', 30, 0, '2024-01-15', '2025-01-15'),
(2, 1, 'B20240201', 20, 2, '2024-02-01', '2025-02-01'),
(3, 1, 'B20240301', 100, 10, '2024-03-01', '2025-03-01'),
(3, 2, 'B20240302', 80, 0, '2024-03-15', '2025-03-15'),
(3, 3, 'B20240303', 60, 5, '2024-03-20', '2025-03-20'),
(4, 1, 'B20240401', 200, 20, '2024-04-01', '2025-04-01'),
(5, 2, 'B20240501', 300, 0, '2024-05-01', '2025-05-01'),
(6, 1, 'B20240601', 500, 50, '2024-06-01', '2024-12-01'),
(6, 3, 'B20240602', 400, 0, '2024-06-15', '2024-12-15'),
(7, 1, 'B20240701', 800, 100, '2024-07-01', '2025-07-01'),
(7, 2, 'B20240702', 600, 50, '2024-07-10', '2025-07-10'),
(8, 1, 'B20240801', 300, 30, '2024-08-01', '2025-08-01'),
(9, 2, 'B20240901', 500, 0, '2024-09-01', '2025-09-01'),
(10, 1, 'B20241001', 100, 10, '2024-10-01', '2025-10-01');

-- 初始化预警数据
INSERT INTO intel_alert (product_id, warehouse_id, alert_type, alert_level, message, status) VALUES
(1, 1, 'SHORTAGE', 'WARN', 'iPhone 15 Pro库存不足，当前库存: 50，安全库存: 10', 0),
(6, 1, 'EXPIRY', 'ERROR', '方便面即将过期，剩余天数: 30天', 0),
(6, 3, 'EXPIRY', 'WARN', '方便面即将过期，剩余天数: 45天', 0),
(2, 1, 'LOW_STOCK', 'INFO', 'MacBook Pro库存偏低，当前库存: 20', 0),
(4, 1, 'OVERSTOCK', 'WARN', '洗衣液库存积压，当前库存: 200，最大库存: 200', 0);
