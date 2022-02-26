package io.shun.tdengine.processor;

import io.shun.tdengine.entity.TDengineFactor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by shunZ on 2021/12/29 上午11:27
 * 查询模型接口
 */
public interface TDengineModel {

    /**
     * 多条查询处理
     * @param c
     * @param sql
     * @param <T>
     * @return
     */
    <T> List<T> executeQuery(Class<T> c, String sql);


    /**
     * 通过sql参数查询多条
     * @param c
     * @param sqlObject
     * @param <T>
     * @return
     */
    <T> List<T> executeQuery(Class<T> c, Map<String, Object> sqlObject);


    /**
     * 查询多条
     * @param c
     * @param where
     * @param <T>
     * @return
     */
    <T> List<T> executeQuery(Class<T> c, TDengineFactor where);

    /**
     * 通过sql参数查询单条
     * @param c
     * @param sqlObject
     * @param <T>
     * @return
     */
    <T> T executeQueryOne(Class<T> c, Map<String, Object> sqlObject);



    /**
     * 单条查询处理
     * @param c
     * @param sql
     * @param <T>
     * @return
     */
    <T> T executeQueryOne(Class<T> c, String sql);


    /**
     * 查询单条
     * @param c
     * @param where
     * @param <T>
     * @return
     */
    <T> T executeQueryOne(Class<T> c, TDengineFactor where);

    /**
     * 插入单条数据
     * @param object
     * @return
     */
    int executeInsert(Object object);


    /**
     * 批量插入数据
     * @param objects
     * @return
     */
    int executeInsert(Collection<?> objects);
}
