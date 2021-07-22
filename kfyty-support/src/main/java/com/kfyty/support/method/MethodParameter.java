package com.kfyty.support.method;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * 描述: 描述方法参数，也可用于单独描述方法
 *
 * @author kfyty725
 * @date 2021/6/3 15:43
 * @email kfyty725@hotmail.com
 */
@Data
@AllArgsConstructor
public class MethodParameter {
    /**
     * 方法所在实例
     */
    private Object source;

    /**
     * 参数所在的方法
     */
    private Method method;

    /**
     * 方法参数
     */
    private Object[] methodArgs;

    /**
     * 原始参数对象
     */
    private Parameter parameter;

    /**
     * 参数类型
     */
    private Class<?> paramType;

    /**
     * 返回值类型
     */
    private Class<?> returnType;

    /**
     * 参数泛型
     */
    private Type parameterGeneric;

    /**
     * 返回值泛型
     */
    private Type returnGeneric;

    /**
     * 参数值，用于描述方法参数时有效
     */
    private Object value;

    public MethodParameter(Method method) {
        this.method = method;
        this.returnType = method.getReturnType();
        this.returnGeneric = method.getGenericReturnType();
    }

    public MethodParameter(Object source, Method method) {
        this(method);
        this.source = source;
    }

    public MethodParameter(Object source, Method method, Object ... methodArgs) {
        this(source, method);
        this.methodArgs = methodArgs;
    }

    public MethodParameter(Method method, Parameter parameter) {
        this(method);
        this.parameter = parameter;
        this.paramType = parameter.getType();
        this.parameterGeneric = parameter.getParameterizedType();
    }

    /**
     * 仅使用参数类型和参数值构造，一般用于后续使用
     * @param paramType 参数类型
     * @param value 参数值
     */
    public MethodParameter(Class<?> paramType, Object value) {
        this.paramType = paramType;
        this.value = value;
    }

    /**
     * 使用声明方法、原参数对象和参数值构造
     * @param method 声明方法
     * @param parameter 原参数对象
     * @param value 参数值
     */
    public MethodParameter(Method method, Parameter parameter, Object value) {
        this(method, parameter);
        this.value = value;
    }
}
