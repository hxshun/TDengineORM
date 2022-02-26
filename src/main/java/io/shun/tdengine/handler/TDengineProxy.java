package io.shun.tdengine.handler;

import io.shun.tdengine.annotation.TDengineSelect;
import io.shun.tdengine.constants.ExceptionMessage;
import io.shun.tdengine.exception.TDengineStatementException;
import io.shun.tdengine.processor.TDengineGenerateStatement;
import io.shun.tdengine.processor.TDengineModel;
import io.shun.tdengine.utils.TDengineSpringContextUtil;

import java.lang.reflect.*;
import java.util.Map;

/**
 * @ClassName: TDengineProxy
 * @Description: 核心代理类 用动态代理实现mapper接口
 * @Author: ShunZ
 * @CreatedAt: 2021-12-27 10:17
 * @Version: 1.0
 **/

public class TDengineProxy<T> implements InvocationHandler {

    private Class<T> mapperInterfaceType;


    public TDengineProxy(Class<T> mapperInterfaceType) {
        this.mapperInterfaceType = mapperInterfaceType;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TDengineModel tDengineModel = TDengineSpringContextUtil.getInterfaceImplement(TDengineModel.class);
        TDengineGenerateStatement tDengineGenerateStatement = TDengineSpringContextUtil.getInterfaceImplement(TDengineGenerateStatement.class);
        Object result = null;
        TDengineSelect select = method.getAnnotation(TDengineSelect.class);
        if (select == null ) { // 判断是否注解
            throw new TDengineStatementException(ExceptionMessage.ANNOTATION_TDENGINESELECT_NOT_FOUNT);
        }

        // 处理sql参数
        Map<String, Object> sqlObject = tDengineGenerateStatement.generateSelectStatement(select.value(), method.getParameters(), args);

        Type genericType = method.getGenericReturnType();
        // 如果是泛型参数的类型
        if(genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Class clazz = Class.forName(pt.getActualTypeArguments()[0].getTypeName());
            result = tDengineModel.executeQuery(clazz, sqlObject);
        } else {
              Class aClass = Class.forName(genericType.getTypeName());
            result = tDengineModel.executeQueryOne(aClass, sqlObject);
        }

        return result;
    }
}
