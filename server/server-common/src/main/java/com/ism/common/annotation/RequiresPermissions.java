package com.ism.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermissions {
    /**
     * 权限标识，多个用逗号分隔，满足其一即可
     * 如: stock:outbound:audit
     */
    String[] value();
}
