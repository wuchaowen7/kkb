package com.ism.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {
    /**
     * 仓库别名的字段名（用于SQL拼接）
     */
    String warehouseAlias() default "w";
}
