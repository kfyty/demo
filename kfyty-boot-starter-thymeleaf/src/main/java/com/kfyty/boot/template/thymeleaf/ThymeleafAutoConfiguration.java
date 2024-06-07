package com.kfyty.boot.template.thymeleaf;

import com.kfyty.boot.template.thymeleaf.expression.DefaultVariableExpressionEvaluator;
import com.kfyty.core.autoconfig.annotation.Autowired;
import com.kfyty.core.autoconfig.annotation.Bean;
import com.kfyty.core.autoconfig.annotation.Configuration;
import com.kfyty.core.autoconfig.condition.annotation.ConditionalOnMissingBean;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Set;

/**
 * 描述: thymeleaf 自动配置
 *
 * @author kfyty725
 * @date 2024/6/05 18:55
 * @email kfyty725@hotmail.com
 */
@Configuration
public class ThymeleafAutoConfiguration {
    @Autowired(required = false)
    private Set<IDialect> dialects;

    @Autowired
    private ThymeleafProperties thymeleafProperties;

    @Autowired(required = false)
    private IStandardVariableExpressionEvaluator standardVariableExpressionEvaluator;

    @Bean
    @ConditionalOnMissingBean
    public ITemplateResolver templateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setCacheable(this.thymeleafProperties.isCacheable());
        templateResolver.setTemplateMode(this.thymeleafProperties.getTemplateMode());
        templateResolver.setCharacterEncoding(this.thymeleafProperties.getCharacterEncoding());
        templateResolver.setPrefix(this.thymeleafProperties.getPrefix());
        templateResolver.setSuffix(this.thymeleafProperties.getSuffix());
        return templateResolver;
    }

    @Bean
    public TemplateEngine templateEngine(ITemplateResolver templateResolver) {
        TemplateEngine templateEngine = new TemplateEngine();
        IDialect dialect = templateEngine.getDialects().iterator().next();              // 默认标准方言
        if (dialect instanceof StandardDialect) {
            ((StandardDialect) dialect).setVariableExpressionEvaluator(new DefaultVariableExpressionEvaluator(this.standardVariableExpressionEvaluator));
        }
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setAdditionalDialects(this.dialects);
        return templateEngine;
    }
}
