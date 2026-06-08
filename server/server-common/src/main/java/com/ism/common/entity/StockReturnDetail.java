package com.ism.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("stock_return_detail")
public class StockReturnDetail {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long returnId;
    
    private Long productId;
    
    private Integer quantity;
    
    private BigDecimal unitPrice;
    
    private String reason;
}