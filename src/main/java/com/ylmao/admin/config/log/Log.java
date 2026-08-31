package com.ylmao.admin.config.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /** 模块标题，写入 operate_title */
    String title() default "";

    /** 日志类型：LOGIN / OPERATE */
    String loggingType() default "OPERATE";

    /** 业务类型：QUERY/ADD/UPDATE/DELETE/LOGIN/LOGOUT/OTHER 等 */
    String businessType() default "OTHER";

    /** 是否保存请求的参数 */
    boolean isSaveRequestData() default true;

    /** 是否保存响应结果 */
    boolean isSaveResponseData() default false;
}
