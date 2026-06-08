package com.ism.intelligence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 数据分析Mapper
 */
@Mapper
public interface AnalysisMapper {

    /**
     * 获取销售趋势数据
     */
    @Select("SELECT DATE(o.create_time) as date, SUM(d.quantity * p.sale_price) as amount " +
            "FROM stock_outbound o " +
            "JOIN stock_outbound_detail d ON o.id = d.outbound_id " +
            "JOIN base_product p ON d.product_id = p.id " +
            "WHERE o.type = 'SALE' AND o.status = 'AUDITED' " +
            "AND o.create_time >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(o.create_time) " +
            "ORDER BY date")
    List<Map<String, Object>> getSalesTrend(@Param("days") Integer days);

    /**
     * 获取热销商品排行
     */
    @Select("SELECT p.name, SUM(d.quantity) as saleCount, SUM(d.quantity * p.sale_price) as revenue " +
            "FROM stock_outbound o " +
            "JOIN stock_outbound_detail d ON o.id = d.outbound_id " +
            "JOIN base_product p ON d.product_id = p.id " +
            "WHERE o.type = 'SALE' AND o.status = 'AUDITED' " +
            "AND o.create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
            "GROUP BY d.product_id " +
            "ORDER BY saleCount DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getTopSelling(@Param("limit") Integer limit);

    /**
     * 获取品类销售分析
     */
    @Select("SELECT c.name, SUM(d.quantity * p.sale_price) as value " +
            "FROM stock_outbound o " +
            "JOIN stock_outbound_detail d ON o.id = d.outbound_id " +
            "JOIN base_product p ON d.product_id = p.id " +
            "JOIN base_category c ON p.category_id = c.id " +
            "WHERE o.type = 'SALE' AND o.status = 'AUDITED' " +
            "AND o.create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
            "GROUP BY c.id, c.name " +
            "ORDER BY value DESC")
    List<Map<String, Object>> getCategoryAnalysis();

    /**
     * 获取库存周转率
     */
    @Select("SELECT c.name, " +
            "ROUND(SUM(d.quantity) / NULLIF(AVG(i.quantity), 0), 2) as rate " +
            "FROM base_category c " +
            "LEFT JOIN base_product p ON c.id = p.category_id " +
            "LEFT JOIN stock_inventory i ON p.id = i.product_id " +
            "LEFT JOIN stock_outbound_detail d ON p.id = d.product_id " +
            "LEFT JOIN stock_outbound o ON d.outbound_id = o.id AND o.type = 'SALE' AND o.status = 'AUDITED' " +
            "WHERE o.create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) OR o.create_time IS NULL " +
            "GROUP BY c.id, c.name " +
            "ORDER BY rate DESC")
    List<Map<String, Object>> getTurnoverRate();

    /**
     * 获取统计概览数据
     */
    @Select("SELECT " +
            "(SELECT COUNT(DISTINCT p.id) FROM base_product p WHERE p.status = 1) as totalProducts, " +
            "(SELECT SUM(i.quantity) FROM stock_inventory i) as totalStock, " +
            "(SELECT COUNT(o.id) FROM stock_outbound o WHERE o.type = 'SALE' AND o.status = 'AUDITED' AND o.create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)) as totalOrders, " +
            "(SELECT SUM(d.quantity * p.sale_price) FROM stock_outbound o JOIN stock_outbound_detail d ON o.id = d.outbound_id JOIN base_product p ON d.product_id = p.id WHERE o.type = 'SALE' AND o.status = 'AUDITED' AND o.create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)) as totalSales, " +
            "(SELECT COUNT(DISTINCT w.id) FROM base_warehouse w) as warehouseCount, " +
            "(SELECT COUNT(o.id) FROM stock_inbound o WHERE o.status = 'PENDING_AUDIT') as pendingInbound, " +
            "(SELECT COUNT(o.id) FROM stock_outbound o WHERE o.status = 'PENDING_AUDIT') as pendingOutbound, " +
            "(SELECT COUNT(a.id) FROM alert_record a WHERE a.status = 0) as alertCount")
    Map<String, Object> getStats();

    /**
     * 获取预警统计数据
     */
    @Select("SELECT " +
            "(SELECT COUNT(id) FROM alert_record WHERE alert_type = 'SHORTAGE' AND status = 0) as stockLow, " +
            "(SELECT COUNT(id) FROM alert_record WHERE alert_type = 'OVERSTOCK' AND status = 0) as stockHigh, " +
            "(SELECT COUNT(id) FROM alert_record WHERE alert_type = 'EXPIRY' AND status = 0) as expire, " +
            "(SELECT COUNT(id) FROM alert_record WHERE alert_type = 'SLOW_MOVING' AND status = 0) as salesSlow")
    Map<String, Object> getAlertStats();
}
