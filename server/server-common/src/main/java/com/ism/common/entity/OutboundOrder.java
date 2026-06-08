package com.ism.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("stock_outbound")
public class OutboundOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String outboundNo;
    private String type; // SALE-销售出库 USE-领用出库
    private String department;
    private Long warehouseId;
    private String status; // DRAFT/PENDING_AUDIT/AUDITED/REJECTED
    private String auditOpinion;
    private String remark;
    private Long creatorId;
    private Long auditorId;
    private LocalDateTime auditTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
