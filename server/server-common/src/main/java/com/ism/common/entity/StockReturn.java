package com.ism.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("stock_return")
public class StockReturn {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String returnNo;
    
    private String type;
    
    private String sourceOrderNo;
    
    private Long warehouseId;
    
    private BigDecimal totalAmount;
    
    private String status;
    
    private String remark;
    
    private Long creatorId;
    
    private LocalDateTime createTime;
}