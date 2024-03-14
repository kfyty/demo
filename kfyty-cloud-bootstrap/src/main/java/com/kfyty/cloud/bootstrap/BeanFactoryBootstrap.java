package com.kfyty.cloud.bootstrap;

import com.kfyty.boot.context.factory.ApplicationContextFactory;
import com.kfyty.cloud.bootstrap.internal.empty.BeanFactoryBootstrapApplication;
import com.kfyty.core.autoconfig.ApplicationContext;
import com.kfyty.core.autoconfig.BeanPostProcessor;
import com.kfyty.core.autoconfig.ConfigurableApplicationContext;
import com.kfyty.core.autoconfig.annotation.Bean;
import com.kfyty.core.autoconfig.aware.ApplicationContextAware;
import com.kfyty.core.autoconfig.aware.BeanFactoryAware;
import com.kfyty.core.autoconfig.aware.ConfigurableApplicationContextAware;
import com.kfyty.core.autoconfig.beans.BeanDefinition;
import com.kfyty.core.autoconfig.boostrap.Bootstrap;
import com.kfyty.core.autoconfig.boostrap.BootstrapConfiguration;
import com.kfyty.core.utils.AnnotationUtil;
import com.kfyty.core.utils.BeanUtil;
import com.kfyty.core.utils.ReflectUtil;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 描述: {@link com.kfyty.core.autoconfig.beans.BeanFactory} 引导
 * 加载 {@link com.kfyty.core.autoconfig.boostrap.BootstrapConfiguration} 标记的配置类，并复制到给定的应用上下文
 *
 * @author kfyty725
 * @date 2023/9/10 22:00
 * @email kfyty725@hotmail.com
 */
public class BeanFactoryBootstrap implements Bootstrap {
    /**
     * 空 bean
     */
    private static final Object EMPTY_BEAN = new Object();

    @Override
    public void bootstrap(ConfigurableApplicationContext applicationContext) throws Exception {
        if (applicationContext.getPrimarySource() == BeanFactoryBootstrapApplication.class) {
            return;
        }
        try (ApplicationContext bootstrapContext = new ApplicationContextFactory().create(applicationContext.getCommandLineArgs(), BeanFactoryBootstrapApplication.class)) {
            bootstrapContext.refresh();
            this.copyBeanFactory(bootstrapContext, applicationContext);
        }
    }

    /**
     * 复制 {@link com.kfyty.core.autoconfig.beans.BeanFactory}
     *
     * @param bootstrapContext   引导 BeanFactory
     * @param applicationContext 启动 BeanFactory
     */
    protected void copyBeanFactory(ApplicationContext bootstrapContext, ConfigurableApplicationContext applicationContext) {
        for (Map.Entry<String, BeanDefinition> entry : bootstrapContext.getBeanDefinitionWithAnnotation(BootstrapConfiguration.class).entrySet()) {
            this.registerBootstrapBean(entry.getKey(), entry.getValue(), bootstrapContext, applicationContext);
        }
    }

    /**
     * 注册引导配置类
     * 被注册的引导配置类，在引导上下文中将被替换为 {@link this#EMPTY_BEAN}，避免销毁引导上下文时被销毁
     *
     * @param beanName           引导配置 bean
     * @param beanDefinition     引导配置 bean definition
     * @param bootstrapContext   引导上下文
     * @param applicationContext 应用上下文
     */
    protected void registerBootstrapBean(String beanName, BeanDefinition beanDefinition, ApplicationContext bootstrapContext, ConfigurableApplicationContext applicationContext) {
        Object bean = bootstrapContext.getBean(beanName);
        applicationContext.registerBeanDefinition(beanName, beanDefinition);
        applicationContext.replaceBean(beanName, bean);
        bootstrapContext.replaceBean(beanName, EMPTY_BEAN);
        if (bean instanceof BeanPostProcessor) {
            applicationContext.registerBeanPostProcessors(beanName, (BeanPostProcessor) bean);
        }
        this.invokeAware(bean, applicationContext);
        for (Method method : ReflectUtil.getMethods(beanDefinition.getBeanType())) {
            Bean beanAnnotation = AnnotationUtil.findAnnotation(method, Bean.class);
            if (beanAnnotation != null) {
                String nestedBeanName = BeanUtil.getBeanName(method, beanAnnotation);
                if (bootstrapContext.containsBeanDefinition(nestedBeanName)) {
                    this.registerBootstrapBean(nestedBeanName, bootstrapContext.getBeanDefinition(nestedBeanName), bootstrapContext, applicationContext);
                }
            }
        }
    }

    /**
     * 执行相关能力接口，注入新的上下文
     *
     * @param bean               引导 bean
     * @param applicationContext 应用上下文
     */
    private void invokeAware(Object bean, ConfigurableApplicationContext applicationContext) {
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(applicationContext);
        }
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(applicationContext);
        }
        if (bean instanceof ConfigurableApplicationContextAware) {
            ((ConfigurableApplicationContextAware) bean).setConfigurableApplicationContext(applicationContext);
        }
    }
}
