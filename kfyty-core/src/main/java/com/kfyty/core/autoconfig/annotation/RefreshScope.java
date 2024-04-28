package com.kfyty.core.autoconfig.annotation;

import com.kfyty.core.autoconfig.beans.BeanDefinition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述: bean 作用域
 *
 * @author kfyty725
 * @date 2021/7/11 10:40
 * @email kfyty725@hotmail.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Scope(BeanDefinition.SCOPE_REFRESH)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RefreshScope {
}