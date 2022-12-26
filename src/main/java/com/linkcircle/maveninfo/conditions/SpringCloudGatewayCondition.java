package com.linkcircle.maveninfo.conditions;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/12/23 17:18
 */

public class SpringCloudGatewayCondition implements Condition {
    private static boolean springCloudGatewayPresent =
            ClassUtils.isPresent("org.springframework.cloud.gateway.config.GatewayAutoConfiguration", null);
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return springCloudGatewayPresent;
    }
}
