package com.ism.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("stock_inventory_log")
public class InventoryLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private Long warehouseId;
    private String bizType; // INBOUND/OUTBOUND/TRANSFER/CHECK
    private String bizNo;
    private Integer changeQty;
    private Integer beforeQty;
    private Integer afterQty;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
