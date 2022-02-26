package io.shun.tdengine.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName: SpringContextUtil
 * @Description: 动态加载bean
 * @Author: ShunZ
 * @CreatedAt: 2021-12-03 14:16
 * @Version: 1.0
 **/
@Component
public class TDengineSpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境
     *
     * @param applicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        TDengineSpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 获取对象
     * @param requiredType
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> requiredType) {

        return applicationContext.getBean(requiredType);
    }


    /**
     * 获取多指定接口的实现类 多个只取一个
     * @param c
     * @param <T>
     * @return
     */
    public static <T> T getInterfaceImplement(Class<T> c) {
        Map<String, T> map = applicationContext.getBeansOfType(c);
        T t = null;
        if (!map.values().isEmpty()) {
            t = (T) map.values().toArray()[0];
        }
        return t ;
    }
}
