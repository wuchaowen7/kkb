package com.ism.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_oper_log")
public class OperLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String businessType;
    private String method;
    private String requestMethod;
    private String operName;
    private String operIp;
    private String operUrl;
    private String operParam;
    private String jsonResult;
    private Integer status; // 0-失败 1-成功
    private String errorMsg;
    private Long costTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
