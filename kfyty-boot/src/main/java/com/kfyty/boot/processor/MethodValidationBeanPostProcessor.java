package com.kfyty.boot.processor;

import com.kfyty.boot.proxy.MethodValidationInterceptorProxy;
import com.kfyty.support.autoconfig.annotation.Autowired;
import com.kfyty.support.autoconfig.annotation.Configuration;
import com.kfyty.support.proxy.AbstractProxyCreatorProcessor;
import com.kfyty.support.utils.AnnotationUtil;
import com.kfyty.support.utils.AopUtil;
import com.kfyty.support.utils.ReflectUtil;
import jakarta.validation.Constraint;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 描述: 方法参数校验
 *
 * @author kfyty725
 * @date 2021/9/25 15:37
 * @email kfyty725@hotmail.com
 */
@Slf4j
@Configuration
public class MethodValidationBeanPostProcessor extends AbstractProxyCreatorProcessor {
    @Autowired
    private Validator validator;

    @Override
    public Object postProcessAfterInstantiation(Object bean, String beanName) {
        if (!this.canValidate(AopUtil.getSourceClass(bean))) {
            return null;
        }
        return this.createProxy(bean, beanName, new MethodValidationInterceptorProxy(this.validator));
    }

    private boolean canValidate(Class<?> clazz) {
        for (Method method : ReflectUtil.getMethods(clazz)) {
            if (AnnotationUtil.hasAnnotation(method, Valid.class) || AnnotationUtil.hasAnnotationElement(method, Constraint.class)) {
                return true;
            }
            if (Arrays.stream(method.getParameters()).anyMatch(e -> AnnotationUtil.hasAnnotation(e, Valid.class) || AnnotationUtil.hasAnnotationElement(e, Constraint.class))) {
                return true;
            }
        }
        return false;
    }
}