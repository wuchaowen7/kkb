package com.ism.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("stock_inbound")
public class InboundOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String inboundNo;
    private String type; // PURCHASE-采购入库 RETURN-退货入库
    private Long supplierId;
    private Long warehouseId;
    private BigDecimal totalAmount;
    private String status; // DRAFT-草稿 CONFIRMED-已确认
    private String remark;
    private Long creatorId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private List<InboundDetail> details;
}
