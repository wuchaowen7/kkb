package com.ism.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("intel_alert")
public class AlertRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private Long warehouseId;
    private String alertType; // SHORTAGE/OVERSTOCK/EXPIRY/SLOW_MOVING
    private String alertLevel; // INFO/WARN/ERROR
    private String message;
    private Integer status; // 0-未处理 1-已处理
    private Long handlerId;
    private LocalDateTime handleTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
