package com.ism.report.service.impl;

import com.alibaba.excel.EasyExcel;
import com.ism.report.entity.InventoryDetailModel;
import com.ism.report.mapper.ReportMapper;
import com.ism.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalProducts", reportMapper.countProducts());
        data.put("totalStock", reportMapper.sumStock());
        data.put("pendingInbound", reportMapper.countInboundByStatus("DRAFT"));
        data.put("pendingOutbound", reportMapper.countOutboundByStatus("PENDING_AUDIT"));
        data.put("alertCount", reportMapper.countAlerts());
        return data;
    }

    @Override
    public Map<String, Object> getInOutStockReport(String startDate, String endDate) {
        Map<String, Object> data = new HashMap<>();
        data.put("startDate", startDate);
        data.put("endDate", endDate);
        data.put("inboundSummary", Collections.emptyList());
        data.put("outboundSummary", Collections.emptyList());
        return data;
    }

    @Override
    public Map<String, Object> getInventoryDetail(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        
        Long productId = params.get("productId") != null ? Long.valueOf(params.get("productId").toString()) : null;
        Long warehouseId = params.get("warehouseId") != null ? Long.valueOf(params.get("warehouseId").toString()) : null;
        Long categoryId = params.get("categoryId") != null ? Long.valueOf(params.get("categoryId").toString()) : null;
        String productCode = (String) params.get("productCode");
        String productName = (String) params.get("productName");
        Integer pageNum = params.get("pageNum") != null ? Integer.valueOf(params.get("pageNum").toString()) : 1;
        Integer pageSize = params.get("pageSize") != null ? Integer.valueOf(params.get("pageSize").toString()) : 20;
        String sortField = (String) params.get("sortField");
        String sortOrder = (String) params.get("sortOrder");
        
        // 处理排序参数
        if (sortField != null && !sortField.isEmpty() && sortOrder != null && !sortOrder.isEmpty()) {
            sortField = mapSortField(sortField);
            if (sortField != null) {
                sortOrder = sortOrder.equalsIgnoreCase("asc") ? "ASC" : "DESC";
            }
        } else {
            sortField = null;
            sortOrder = null;
        }
        
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 查询总数
        Long total = reportMapper.countInventoryDetail(productId, warehouseId, categoryId, productCode, productName);
        
        // 分页查询
        List<Map<String, Object>> list = reportMapper.getInventoryDetailList(
            productId, warehouseId, categoryId, productCode, productName, sortField, sortOrder, offset, pageSize);
        
        // 处理日期格式
        for (Map<String, Object> item : list) {
            if (item.get("production_date") instanceof java.sql.Date) {
                item.put("productionDate", item.get("production_date").toString());
                item.remove("production_date");
            }
            if (item.get("expiry_date") instanceof java.sql.Date) {
                item.put("expiryDate", item.get("expiry_date").toString());
                item.remove("expiry_date");
            }
            // 处理驼峰命名
            if (item.get("product_id") != null) {
                item.put("productId", item.get("product_id"));
                item.remove("product_id");
            }
            if (item.get("warehouse_id") != null) {
                item.put("warehouseId", item.get("warehouse_id"));
                item.remove("warehouse_id");
            }
            if (item.get("category_id") != null) {
                item.put("categoryId", item.get("category_id"));
                item.remove("category_id");
            }
            if (item.get("warehouse_name") != null) {
                item.put("warehouseName", item.get("warehouse_name"));
                item.remove("warehouse_name");
            }
            if (item.get("category_name") != null) {
                item.put("categoryName", item.get("category_name"));
                item.remove("category_name");
            }
            if (item.get("batch_no") != null) {
                item.put("batchNo", item.get("batch_no"));
                item.remove("batch_no");
            }
            if (item.get("locked_quantity") != null) {
                item.put("lockedQuantity", item.get("locked_quantity"));
                item.remove("locked_quantity");
            }
        }
        
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        
        return result;
    }
    
    private String mapSortField(String field) {
        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("code", "p.code");
        fieldMap.put("name", "p.name");
        fieldMap.put("categoryName", "c.name");
        fieldMap.put("warehouseName", "w.name");
        fieldMap.put("batchNo", "i.batch_no");
        fieldMap.put("quantity", "i.quantity");
        fieldMap.put("lockedQuantity", "i.locked_quantity");
        fieldMap.put("productionDate", "i.production_date");
        fieldMap.put("expiryDate", "i.expiry_date");
        return fieldMap.get(field);
    }

    @Override
    public String exportToExcel(String type) {
        try {
            // 创建导出目录
            String exportDir = System.getProperty("java.io.tmpdir") + "export" + File.separator;
            File dir = new File(exportDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 生成文件名
            String fileName = exportDir + "inventory_detail_" + System.currentTimeMillis() + ".xlsx";
            
            if ("inventory-detail".equals(type)) {
                // 查询所有库存明细数据
                List<Map<String, Object>> allData = reportMapper.getInventoryDetailList(
                    null, null, null, null, null, "i.quantity", "DESC", 0, Integer.MAX_VALUE);
                
                // 转换为导出模型
                List<InventoryDetailModel> exportData = new ArrayList<>();
                for (Map<String, Object> item : allData) {
                    InventoryDetailModel model = new InventoryDetailModel();
                    model.setCode(getStringValue(item, "code"));
                    model.setName(getStringValue(item, "name"));
                    model.setCategoryName(getStringValue(item, "category_name"));
                    model.setSpec(getStringValue(item, "spec"));
                    model.setUnit(getStringValue(item, "unit"));
                    model.setWarehouseName(getStringValue(item, "warehouse_name"));
                    model.setBatchNo(getStringValue(item, "batch_no"));
                    model.setQuantity(getIntValue(item, "quantity"));
                    model.setLockedQuantity(getIntValue(item, "locked_quantity"));
                    model.setAvailableQuantity(model.getQuantity() - (model.getLockedQuantity() != null ? model.getLockedQuantity() : 0));
                    model.setSafetyStock(getIntValue(item, "safetyStock"));
                    model.setMaxStock(getIntValue(item, "maxStock"));
                    
                    // 处理日期
                    if (item.get("production_date") != null) {
                        model.setProductionDate(item.get("production_date").toString());
                    }
                    if (item.get("expiry_date") != null) {
                        model.setExpiryDate(item.get("expiry_date").toString());
                    }
                    
                    // 计算库存状态
                    model.setStatus(calculateStatus(model));
                    
                    exportData.add(model);
                }
                
                // 使用EasyExcel导出
                EasyExcel.write(fileName, InventoryDetailModel.class)
                    .sheet("库存明细")
                    .doWrite(exportData);
                
                return fileName;
            }
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
    
    private Integer getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return 0;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof BigDecimal) return ((BigDecimal) value).intValue();
        if (value instanceof Number) return ((Number) value).intValue();
        return 0;
    }
    
    private String calculateStatus(InventoryDetailModel model) {
        // 检查过期
        if (model.getExpiryDate() != null && !model.getExpiryDate().isEmpty()) {
            try {
                Date expiry = new SimpleDateFormat("yyyy-MM-dd").parse(model.getExpiryDate());
                Date now = new Date();
                long diffDays = (expiry.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
                if (diffDays <= 0) return "已过期";
                if (diffDays <= 30) return "临期";
            } catch (Exception e) {
                // 忽略日期解析错误
            }
        }
        
        // 检查库存不足
        if (model.getQuantity() <= model.getSafetyStock()) return "库存不足";
        
        // 检查积压
        if (model.getQuantity() > model.getMaxStock()) return "库存积压";
        
        return "正常";
    }
}
