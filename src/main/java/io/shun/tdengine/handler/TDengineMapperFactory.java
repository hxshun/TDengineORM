package io.shun.tdengine.handler;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @ClassName: TDengineMapperFactory
 * @Description: 实例化TDengineMapper接口的工厂类
 * @Author: ShunZ
 * @CreatedAt: 2021-12-30 14:05
 * @Version: 1.0
 **/

public class TDengineMapperFactory<T> implements FactoryBean<T> {

    private Class<T> mapperInterfaceType;


    public TDengineMapperFactory(Class<T> mapperInterfaceType) {
        this.mapperInterfaceType = mapperInterfaceType;
    }

    @Override
    public T getObject() throws Exception {
        InvocationHandler handler = new TDengineProxy<>(mapperInterfaceType);
        return mapperInterfaceType.cast(
                Proxy.newProxyInstance(mapperInterfaceType.getClassLoader(),
                        new Class[] {mapperInterfaceType}, handler));
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
