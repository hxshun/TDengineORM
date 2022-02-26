package io.shun.tdengine.processor;

import io.shun.tdengine.constants.TDengineStatementContent;
import io.shun.tdengine.datasource.TDataSourceConnection;
import io.shun.tdengine.entity.TDengineFactor;
import io.shun.tdengine.exception.TDengineStatementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.lang.reflect.Field;

import java.sql.*;
import java.util.*;


/**
 * @ClassName: TDengineModel
 * @Description: TDengine实际数据库操作类
 * @Author: ShunZ
 * @CreatedAt: 2021-12-01 17:28
 * @Version: 1.0
 **/
@Component
public class TDengineModelImpl implements TDengineModel {


    @Autowired
    TDataSourceConnection tDataSourceConnection;

    @Autowired
    TDengineGenerateStatement tDengineGenerateStatement;


    /**
     * 根据数据类型设置对应的字段
     * @param obj
     * @param field
     * @param resultSet
     * @param columnName
     * @throws SQLException
     * @throws IllegalAccessException
     */
    private void setResultByDataType(Object obj, Field field, ResultSet resultSet, String columnName) throws SQLException, IllegalAccessException
    {
        if (field.getType().equals(Long.class)) {
            field.set(obj, resultSet.getLong(columnName));
        } else if (field.getType().equals(Integer.class)) {
            field.set(obj, resultSet.getInt(columnName));
        } else if (field.getType().equals(Double.class)) {
            field.set(obj, resultSet.getDouble(columnName));
        } else if (field.getType().equals(Float.class)) {
            field.set(obj, resultSet.getFloat(columnName));
        } else if (field.getType().equals(String.class)) {
            field.set(obj, resultSet.getString(columnName));;
        }
    }


    /**
     * 处理返回结果集
     * @param c
     * @param resultSet
     * @param fields
     * @param <T>
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private <T> List<T> resultToSet(Class<T> c, ResultSet resultSet, Field[] fields) throws SQLException, IllegalAccessException, InstantiationException
    {
        List<T> result = new ArrayList<>();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        while (resultSet.next()) {
            T obj = c.newInstance();
            for (int i = 0; i < columnCount; i++) {
                String columnName = resultSetMetaData.getColumnName(i + 1);

                for (Field field : fields) {
                    field.setAccessible(true);
                    if (field.getName().equalsIgnoreCase(TDStatementTool.underlineToSmallCamel(columnName))) {
                        setResultByDataType(obj, field, resultSet, columnName);
                    }
                }
            }
            result.add(obj);
        }
        return result;
    }

    /**
     * 处理返回对象
     * @param c
     * @param resultSet
     * @param fields
     * @param <T>
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private <T> T resultToObject(Class<T> c, ResultSet resultSet, Field[] fields) throws SQLException, IllegalAccessException, InstantiationException
    {
        T result = c.newInstance();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        if (resultSet.next()) {
            for (int i = 0; i < columnCount; i++) {
                String columnName = resultSetMetaData.getColumnName(i + 1);

                for (Field field : fields) {
                    field.setAccessible(true);
                    if (field.getName().equalsIgnoreCase(TDStatementTool.underlineToSmallCamel(columnName))) {
                        setResultByDataType(result, field, resultSet, columnName);
                    }
                }
            }
        }
        return result;
    }


    /**
     * 根据sql参数查询结果
     * @param parameters
     * @param preparedStatement
     * @return
     * @throws SQLException
     */
    private ResultSet executeQueryByParameters(Map<Integer, Object> parameters, PreparedStatement preparedStatement) throws SQLException
    {
        // 对参数位置进行排序
        Integer[] paramKeys = parameters.keySet().toArray(new Integer[0]);
        Arrays.sort(paramKeys);

        // 根据位置映射参数值
        for (int i = 0; i < paramKeys.length; i++) {
            int key = paramKeys[i];
            Object value = parameters.get(key);
            if (value instanceof String) {
//                value = String.format(TDengineStatementContent.APOS, value);
                preparedStatement.setString(i + 1, value.toString());
            } else {
                preparedStatement.setObject(i + 1, value);
            }

        }

        return preparedStatement.executeQuery();
    }


    /**
     * 处理插入数据
     * @param sql
     * @return
     */
    private int insertProcessor(String sql)
    {
        int result = 0;
        if (!TDStatementTool.isEmpty(sql)) {
            try (Connection connection = tDataSourceConnection.getConnection();
                 Statement statement = connection.createStatement();) {
                result = statement.executeUpdate(sql);
            } catch (SQLException e) {
                throw new TDengineStatementException(e.getMessage());
            }
        }
        return result;
    }


    @Override
    public <T> List<T> executeQuery(Class<T> c, String sql)
    {
        List<T> result = new ArrayList<>();

        if (!TDStatementTool.isEmpty(sql)) {
            Field[] fields = c.getDeclaredFields();

            try(Connection connection = tDataSourceConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql))
            {
                result = resultToSet(c, resultSet, fields);

            } catch (SQLException e) {

                throw new TDengineStatementException(e.getMessage());

            } catch (IllegalAccessException | InstantiationException e) {

                e.printStackTrace();
            }
        }

        return result;
    }


    @Override
    public <T> List<T> executeQuery(Class<T> c, Map<String, Object> sqlObject)
    {

        List<T> result = new ArrayList<>();

        String sql = sqlObject.get(TDengineStatementContent.SQL_OBJECT_MAP_STATEMENT).toString();

        Map<Integer, Object> parameters = (Map<Integer, Object>) sqlObject.get(TDengineStatementContent.SQL_OBJECT_MAP_PARAMS);

        if (!TDStatementTool.isEmpty(sql)) {
            Field[] fields = c.getDeclaredFields();
            try(Connection connection = tDataSourceConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = executeQueryByParameters(parameters, preparedStatement))
            {
                result = resultToSet(c, resultSet, fields);

            } catch (SQLException e) {

                throw new TDengineStatementException(e.getMessage());

            } catch (IllegalAccessException | InstantiationException e) {

                e.printStackTrace();
            }
        }

        return result;
    }


    @Override
    public <T> List<T> executeQuery(Class<T> c, TDengineFactor where)
    {

        String sql = tDengineGenerateStatement.generateSelectStatement(c, where);

        List<T> result = executeQuery(c, sql);

        return result;
    }


    @Override
    public <T> T executeQueryOne(Class<T> c, Map<String, Object> sqlObject)
    {

        T result;

        String sql = sqlObject.get(TDengineStatementContent.SQL_OBJECT_MAP_STATEMENT).toString();

        Map<Integer, Object> parameters = (Map<Integer, Object>) sqlObject.get(TDengineStatementContent.SQL_OBJECT_MAP_PARAMS);

        try {
            result = c.newInstance();
            Field[] fields = c.getDeclaredFields();

            if (!TDStatementTool.isEmpty(sql)) {
                try(Connection connection = tDataSourceConnection.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    ResultSet resultSet = executeQueryByParameters(parameters, preparedStatement);)
                {

                    result = resultToObject(c, resultSet, fields);

                } catch (SQLException e) {

                    throw new TDengineStatementException(e.getMessage());
                }
            }
        } catch (IllegalAccessException | InstantiationException e) {

            throw new TDengineStatementException(e.getMessage());
        }

        return result;
    }


    @Override
    public <T> T executeQueryOne(Class<T> c, String sql)
    {
        T result;
        try {
            result = c.newInstance();
            Field[] fields = c.getDeclaredFields();

            if (!TDStatementTool.isEmpty(sql)) {
                try(Connection connection = tDataSourceConnection.getConnection();
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql))
                {
                    result = resultToObject(c, resultSet, fields);

                } catch (SQLException e) {

                    throw new TDengineStatementException(e.getMessage());
                }
            }
        } catch (IllegalAccessException | InstantiationException e) {

            throw new TDengineStatementException(e.getMessage());
        }

        return result;
    }

    @Override
    public <T> T executeQueryOne(Class<T> c, TDengineFactor where)
    {

        String sql = tDengineGenerateStatement.generateSelectStatement(c, where);

        T result = executeQueryOne(c, sql);

        return result;
    }

    @Override
    public int executeInsert(Object object)
    {

        String sql = tDengineGenerateStatement.generateInsertStatement(object);

        return insertProcessor(sql);
    }


    @Override
    public int executeInsert(Collection<?> objects)
    {

        String sql = tDengineGenerateStatement.generateInsertStatement(objects);

        return insertProcessor(sql);
    }
}
