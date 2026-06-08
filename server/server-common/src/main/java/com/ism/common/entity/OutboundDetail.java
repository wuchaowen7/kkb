package com.ism.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("stock_outbound_detail")
public class OutboundDetail {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long outboundId;
    private Long productId;
    private Integer quantity;
    private Long inventoryId;
}
