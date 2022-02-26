package io.shun.tdengine.annotation;

import org.springframework.stereotype.Repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by shunZ on 2021/12/27 上午10:12
 * 定义需要被动态代理实现接口的注解
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repository
public @interface TDengineRepository {
}
