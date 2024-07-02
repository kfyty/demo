package com.kfyty.loveqq.framework.data.cache.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述: 移除缓存注解
 *
 * @author kfyty725
 * @date 2024/7/4 9:52
 * @email kfyty725@hotmail.com
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheClear {
    /**
     * 缓存名称，可使用 ognl 表达式
     *
     * @return cache name
     */
    String value() default "";

    /**
     * 放入缓存的条件，可使用 ognl 表达式
     *
     * @return 条件表达式
     */
    String condition() default "";
}
