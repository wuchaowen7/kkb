package com.ism.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("stock_check")
public class CheckOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String checkNo;
    private Long warehouseId;
    private Integer totalCount; // 商品种类数
    private Integer diffCount; // 差异种类数
    private String status; // CHECKING/CONFIRMED
    private String remark;
    private Long creatorId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
