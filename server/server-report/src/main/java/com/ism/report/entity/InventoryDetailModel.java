package com.ism.report.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class InventoryDetailModel {
    
    @ExcelProperty("商品编码")
    private String code;
    
    @ExcelProperty("商品名称")
    private String name;
    
    @ExcelProperty("分类")
    private String categoryName;
    
    @ExcelProperty("规格型号")
    private String spec;
    
    @ExcelProperty("单位")
    private String unit;
    
    @ExcelProperty("仓库")
    private String warehouseName;
    
    @ExcelProperty("批次号")
    private String batchNo;
    
    @ExcelProperty("库存数量")
    private Integer quantity;
    
    @ExcelProperty("锁定数量")
    private Integer lockedQuantity;
    
    @ExcelProperty("可用库存")
    private Integer availableQuantity;
    
    @ExcelProperty("安全库存")
    private Integer safetyStock;
    
    @ExcelProperty("最大库存")
    private Integer maxStock;
    
    @ExcelProperty("生产日期")
    private String productionDate;
    
    @ExcelProperty("过期日期")
    private String expiryDate;
    
    @ExcelProperty("库存状态")
    private String status;
}
