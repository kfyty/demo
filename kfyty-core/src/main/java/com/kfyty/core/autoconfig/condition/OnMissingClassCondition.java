package com.kfyty.core.autoconfig.condition;

import com.kfyty.core.autoconfig.condition.annotation.ConditionalOnMissingClass;
import com.kfyty.core.wrapper.AnnotationWrapper;

/**
 * 描述:
 *
 * @author kfyty725
 * @date 2022/4/17 11:43
 * @email kfyty725@hotmail.com
 */
public class OnMissingClassCondition extends OnClassCondition {

    @Override
    public boolean isMatch(ConditionContext context, AnnotationWrapper<?> metadata) {
        return !super.isMatch(context, metadata);
    }

    @Override
    protected String[] conditionNames(AnnotationWrapper<?> metadata) {
        return ((ConditionalOnMissingClass) metadata.get()).value();
    }
}