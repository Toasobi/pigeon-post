package com.seeing.pigeon.handler.flowcontrol.annotations;

import com.seeing.pigeon.handler.enums.RateLimitStrategy;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * 单机限流注解
 */
@Target({ElementType.TYPE}) //这个注解指定 @LocalRateLimit 可以应用于类、接口（包括注解类型）或枚举声明
@Retention(RetentionPolicy.RUNTIME) //这个注解指定 @LocalRateLimit 将在运行时保留，因此可以通过反射机制读取
@Documented //应该被包含在 javadoc 中
@Service //将被Spring自动检测并注册为Spring的Bean
public @interface LocalRateLimit {
    //定义了一个名为 rateLimitStrategy 的属性，类型为 RateLimitStrategy 枚举，默认值为 RateLimitStrategy.REQUEST_RATE_LIMIT
    RateLimitStrategy rateLimitStrategy() default RateLimitStrategy.REQUEST_RATE_LIMIT;
}
