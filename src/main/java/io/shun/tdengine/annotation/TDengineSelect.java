package io.shun.tdengine.annotation;

import java.lang.annotation.*;

/**
 * Created by shunZ on 2021/12/27 上午10:09
 * 自定义sql 查询注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TDengineSelect {
    String value() default "";
}
