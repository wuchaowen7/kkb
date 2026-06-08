package com.ism.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("base_product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String name;
    private Long categoryId;
    private String spec;
    private String unit;
    private BigDecimal costPrice;
    private BigDecimal salePrice;
    private Integer safetyStock;
    private Integer maxStock;
    private Integer expiryDays;
    private LocalDate productionDate;
    private String barcode;
    private Integer status; // 0-下架 1-上架
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
