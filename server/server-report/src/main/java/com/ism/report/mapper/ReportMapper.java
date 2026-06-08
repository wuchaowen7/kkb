package com.ism.report.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {

    @Select("SELECT COUNT(*) FROM base_product WHERE status = 1")
    Long countProducts();

    @Select("SELECT COALESCE(SUM(quantity), 0) FROM stock_inventory")
    Long sumStock();

    @Select("SELECT COUNT(*) FROM stock_inbound WHERE status = #{status}")
    Long countInboundByStatus(String status);

    @Select("SELECT COUNT(*) FROM stock_outbound WHERE status = #{status}")
    Long countOutboundByStatus(String status);

    @Select("SELECT COUNT(*) FROM intel_alert WHERE status = 0")
    Long countAlerts();

    @Select("<script>" +
            "SELECT i.id, i.product_id, p.code, p.name, p.spec, p.unit, " +
            "p.category_id, c.name as category_name, " +
            "i.warehouse_id, w.name as warehouse_name, i.batch_no, " +
            "i.quantity, i.locked_quantity, i.production_date, i.expiry_date, " +
            "p.safety_stock as safetyStock, p.max_stock as maxStock " +
            "FROM stock_inventory i " +
            "JOIN base_product p ON i.product_id = p.id " +
            "JOIN base_warehouse w ON i.warehouse_id = w.id " +
            "LEFT JOIN base_category c ON p.category_id = c.id " +
            "<where>" +
            "<if test='productId != null'>AND i.product_id = #{productId}</if>" +
            "<if test='warehouseId != null'>AND i.warehouse_id = #{warehouseId}</if>" +
            "<if test='categoryId != null'>AND p.category_id = #{categoryId}</if>" +
            "<if test='productCode != null and productCode != \"\"'>AND p.code LIKE CONCAT('%', #{productCode}, '%')</if>" +
            "<if test='productName != null and productName != \"\"'>AND p.name LIKE CONCAT('%', #{productName}, '%')</if>" +
            "</where>" +
            "<if test='sortField != null and sortField != \"\"'>" +
            "ORDER BY ${sortField} ${sortOrder}" +
            "</if>" +
            "<if test='(sortField == null or sortField == \"\") and (sortOrder == null or sortOrder == \"\")'>" +
            "ORDER BY i.quantity DESC" +
            "</if>" +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Map<String, Object>> getInventoryDetailList(@Param("productId") Long productId, 
                                                    @Param("warehouseId") Long warehouseId,
                                                    @Param("categoryId") Long categoryId,
                                                    @Param("productCode") String productCode,
                                                    @Param("productName") String productName,
                                                    @Param("sortField") String sortField,
                                                    @Param("sortOrder") String sortOrder,
                                                    @Param("offset") Integer offset,
                                                    @Param("pageSize") Integer pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) " +
            "FROM stock_inventory i " +
            "JOIN base_product p ON i.product_id = p.id " +
            "JOIN base_warehouse w ON i.warehouse_id = w.id " +
            "<where>" +
            "<if test='productId != null'>AND i.product_id = #{productId}</if>" +
            "<if test='warehouseId != null'>AND i.warehouse_id = #{warehouseId}</if>" +
            "<if test='categoryId != null'>AND p.category_id = #{categoryId}</if>" +
            "<if test='productCode != null and productCode != \"\"'>AND p.code LIKE CONCAT('%', #{productCode}, '%')</if>" +
            "<if test='productName != null and productName != \"\"'>AND p.name LIKE CONCAT('%', #{productName}, '%')</if>" +
            "</where>" +
            "</script>")
    Long countInventoryDetail(@Param("productId") Long productId, 
                              @Param("warehouseId") Long warehouseId,
                              @Param("categoryId") Long categoryId,
                              @Param("productCode") String productCode,
                              @Param("productName") String productName);
}
