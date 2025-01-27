package com.kfyty.loveqq.framework.javafx.core.autoconfig;

import com.kfyty.loveqq.framework.core.autoconfig.annotation.Bean;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.ComponentFilter;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.ComponentScan;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Configuration;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Order;
import com.kfyty.loveqq.framework.javafx.core.BootstrapApplication;
import com.kfyty.loveqq.framework.javafx.core.annotation.FController;
import com.kfyty.loveqq.framework.javafx.core.factory.ControllerFactory;
import com.kfyty.loveqq.framework.javafx.core.processor.FControllerBeanFactoryPostProcessor;
import com.kfyty.loveqq.framework.javafx.core.processor.FControllerBeanPostProcessor;

/**
 * 描述: javafx 自动配置
 *
 * @author kfyty725
 * @date 2024/2/21 11:56
 * @email kfyty725@hotmail.com
 */
@Configuration
@ComponentScan(includeFilter = @ComponentFilter(annotations = FController.class))
public class JavaFXAutoConfig {

    @Bean
    public ControllerFactory controllerFactory() {
        return new ControllerFactory();
    }

    @Bean
    @Order
    public FControllerBeanFactoryPostProcessor fControllerBeanFactoryPostProcessor() {
        return new FControllerBeanFactoryPostProcessor();
    }

    @Bean
    @Order(Integer.MAX_VALUE)
    public FControllerBeanPostProcessor fControllerBeanPostProcessor() {
        return new FControllerBeanPostProcessor();
    }

    @Bean
    @Order(Integer.MAX_VALUE)
    public BootstrapApplication bootstrapApplication() {
        return new BootstrapApplication();
    }
}
