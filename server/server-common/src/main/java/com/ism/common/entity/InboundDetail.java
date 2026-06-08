package com.ism.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("stock_inbound_detail")
public class InboundDetail {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long inboundId;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String batchNo;
    private LocalDate productionDate;
    private LocalDate expiryDate;
}
