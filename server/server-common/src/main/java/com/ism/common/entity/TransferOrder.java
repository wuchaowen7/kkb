package com.ism.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("stock_transfer")
public class TransferOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String transferNo;
    private Long fromWarehouseId;
    private Long toWarehouseId;
    private Long productId;
    private Integer quantity;
    private String status; // PENDING/CONFIRMED
    private String remark;
    private Long creatorId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
