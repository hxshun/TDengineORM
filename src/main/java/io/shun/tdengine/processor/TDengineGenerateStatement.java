package io.shun.tdengine.processor;

import io.shun.tdengine.entity.TDengineFactor;


import java.lang.reflect.Parameter;
import java.util.*;


/**
 * Created by shunZ on 2021/12/29 上午11:27
 * sql语句生成
 */
public interface TDengineGenerateStatement {
    /**
     * 生成查询语句
     * @param c
     * @param where
     * @return
     */
     String generateSelectStatement(Class c, TDengineFactor where);


    /**
     * 生成sql 和 参数
     * @param sql
     * @param parameters
     * @return
     */
    Map<String, Object> generateSelectStatement(String sql, Parameter[] parameters, Object[] args);


    /**
     * 生成单条插入语句
     * @param object
     * @return
     */
     String generateInsertStatement(Object object);


    /**
     * 批量插入语句
     * @param objects
     * @return
     */
     String generateInsertStatement(Collection<?> objects);
}
