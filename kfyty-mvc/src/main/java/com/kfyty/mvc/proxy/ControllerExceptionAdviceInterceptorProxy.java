package com.kfyty.mvc.proxy;

import com.kfyty.mvc.annotation.ExceptionHandler;
import com.kfyty.mvc.annotation.RequestMapping;
import com.kfyty.mvc.request.resolver.HandlerMethodReturnValueProcessor;
import com.kfyty.mvc.request.support.ModelViewContainer;
import com.kfyty.mvc.servlet.DispatcherServlet;
import com.kfyty.support.autoconfig.ApplicationContext;
import com.kfyty.support.method.MethodParameter;
import com.kfyty.support.proxy.InterceptorChain;
import com.kfyty.support.proxy.InterceptorChainPoint;
import com.kfyty.support.proxy.MethodProxyWrapper;
import com.kfyty.support.utils.AnnotationUtil;
import com.kfyty.support.utils.CommonUtil;
import com.kfyty.support.utils.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 *
 * @author kfyty725
 * @date 2021/6/18 10:29
 * @email kfyty725@hotmail.com
 */
@Slf4j
public class ControllerExceptionAdviceInterceptorProxy implements InterceptorChainPoint {
    private final DispatcherServlet dispatcherServlet;
    private final Map<Class<? extends Throwable>, MethodParameter> exceptionHandlerMap;

    public ControllerExceptionAdviceInterceptorProxy(ApplicationContext context, List<Object> controllerAdviceBeans) {
        this.dispatcherServlet = context.getBean(DispatcherServlet.class);
        this.exceptionHandlerMap = new LinkedHashMap<>();
        this.initExceptionHandler(controllerAdviceBeans);
    }

    @SuppressWarnings("unchecked")
    private void initExceptionHandler(List<Object> controllerAdviceBeans) {
        for (Object adviceBean : controllerAdviceBeans) {
            for (Method method : ReflectUtil.getMethods(adviceBean.getClass())) {
                ExceptionHandler annotation = AnnotationUtil.findAnnotation(method, ExceptionHandler.class);
                if (annotation != null) {
                    Class<?>[] exceptionClasses = CommonUtil.notEmpty(annotation.value()) ? annotation.value() : method.getParameterTypes();
                    for (Class<?> exceptionClass : exceptionClasses) {
                        if (Throwable.class.isAssignableFrom(exceptionClass)) {
                            this.exceptionHandlerMap.put((Class<? extends Throwable>) exceptionClass, new MethodParameter(adviceBean, method));
                        }
                    }
                }
            }
        }
    }

    @Override
    public Object proceed(MethodProxyWrapper methodProxy, InterceptorChain chain) throws Throwable {
        Method sourceMethod = methodProxy.getSourceMethod();
        if(!AnnotationUtil.hasAnnotationElement(sourceMethod, RequestMapping.class)) {
            return chain.proceed(methodProxy);
        }
        try {
            return chain.proceed(methodProxy);
        } catch (Throwable throwable) {
            MethodParameter controllerExceptionAdvice = this.findControllerExceptionAdvice(throwable);
            if(controllerExceptionAdvice != null) {
                this.processControllerAdvice(controllerExceptionAdvice, throwable);
                return null;
            }
            throw throwable;
        }
    }

    private MethodParameter findControllerExceptionAdvice(Throwable throwable) {
        MethodParameter handlerMethod = this.exceptionHandlerMap.get(throwable.getClass());
        if (handlerMethod == null) {
            for (Map.Entry<Class<? extends Throwable>, MethodParameter> entry : this.exceptionHandlerMap.entrySet()) {
                if (entry.getKey().isAssignableFrom(throwable.getClass())) {
                    handlerMethod = entry.getValue();
                    break;
                }
            }
        }
        if(handlerMethod == null) {
            return null;
        }
        Parameter[] parameters = handlerMethod.getMethod().getParameters();
        Object[] exceptionArgs = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if(parameters[i].getType().isAssignableFrom(throwable.getClass())) {
                exceptionArgs[i] = throwable;
            }
        }
        return new MethodParameter(handlerMethod.getSource(), handlerMethod.getMethod(), exceptionArgs);
    }

    private void processControllerAdvice(MethodParameter exceptionAdviceMethod, Throwable throwable) throws Throwable {
        Object retValue = ReflectUtil.invokeMethod(exceptionAdviceMethod.getSource(), exceptionAdviceMethod.getMethod(), exceptionAdviceMethod.getMethodArgs());
        for (HandlerMethodReturnValueProcessor returnValueProcessor : this.dispatcherServlet.getReturnValueProcessors()) {
            if(returnValueProcessor.supportsReturnType(exceptionAdviceMethod)) {
                ModelViewContainer container = new ModelViewContainer().setPrefix(dispatcherServlet.getPrefix()).setSuffix(dispatcherServlet.getSuffix());
                returnValueProcessor.handleReturnValue(retValue, exceptionAdviceMethod, container);
                return;
            }
        }
        log.warn("can't parse return value temporarily, no return value processor support !");
        throw throwable;
    }
}