package com.kfyty.boot.processor.factory;

import com.kfyty.boot.processor.factory.internal.HardCodeBeanFactoryPostProcessor;
import com.kfyty.core.autoconfig.BeanFactoryPostProcessor;
import com.kfyty.core.autoconfig.annotation.Component;
import com.kfyty.core.autoconfig.beans.BeanDefinition;
import com.kfyty.core.autoconfig.beans.BeanFactory;
import com.kfyty.core.event.ApplicationListener;
import com.kfyty.core.event.ContextRefreshedEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.kfyty.core.autoconfig.beans.builder.BeanDefinitionBuilder.factoryBeanDefinition;

/**
 * 描述: {@link com.kfyty.core.autoconfig.beans.FactoryBean} 处理
 *
 * @author kfyty725
 * @date 2022/10/23 15:30
 * @email kfyty725@hotmail.com
 */
@Component
public class FactoryBeanBeanFactoryPostProcessor extends HardCodeBeanFactoryPostProcessor implements BeanFactoryPostProcessor, ApplicationListener<ContextRefreshedEvent> {
    protected volatile Set<String> postProcessed;

    @Override
    public void postProcessBeanFactory(BeanFactory beanFactory) {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>(beanFactory.getBeanDefinitions());
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : beanDefinitionMap.entrySet()) {
            this.postProcessBeanDefinition(beanDefinitionEntry.getValue(), beanFactory);
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (this.postProcessed != null) {
            this.postProcessed.clear();
            this.postProcessed = null;
        }
    }

    public BeanDefinition postProcessBeanDefinition(BeanDefinition beanDefinition, BeanFactory beanFactory) {
        if (beanDefinition.isAutowireCandidate() && beanDefinition.isFactoryBean()) {
            if (!this.addPostProcessed(beanDefinition.getBeanName())) {
                BeanDefinition factoryBeanDefinition = factoryBeanDefinition(beanDefinition).getBeanDefinition();
                beanFactory.registerBeanDefinition(factoryBeanDefinition, false);
                return factoryBeanDefinition;
            }
        }
        return beanDefinition;
    }

    protected boolean addPostProcessed(String beanName) {
        if (this.postProcessed == null) {
            synchronized (this) {
                if (this.postProcessed == null) {
                    this.postProcessed = new HashSet<>();
                }
            }
        }
        return !this.postProcessed.add(beanName);
    }
}
