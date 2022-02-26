package io.shun.tdengine.processor;

import io.shun.tdengine.constants.ExceptionMessage;
import io.shun.tdengine.constants.TDengineStatementContent;
import io.shun.tdengine.entity.TDengineFactor;
import io.shun.tdengine.exception.TDataSourceException;
import io.shun.tdengine.exception.TDengineStatementException;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: TDengineGenerateStatementImpl
 * @Description: sql语句生成类
 * @Author: ShunZ
 * @CreatedAt: 2021-12-30 17:26
 * @Version: 1.0
 **/

public class TDengineGenerateStatementImpl implements TDengineGenerateStatement {
    /**
     * 处理表名和TAGS标签
     * @param object
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private static String generateTableAndTags(Object object) throws NoSuchFieldException, IllegalAccessException {
        StringBuffer sql = new StringBuffer();
        Class c = object.getClass();
        Class superClass = c.getSuperclass();
        // 提取表前缀
        Field prefix = superClass.getDeclaredField(TDengineStatementContent.FIELD_NAME_TABLE_PREFIX);
        prefix.setAccessible(true);
        String tablePrefix = TDStatementTool.ifNull(prefix.get(object)) ? TDengineStatementContent.EMPTY_STRING : prefix.get(object).toString();
        String sTableName = TDStatementTool.camelToUnderline(c.getSimpleName(), false);
        String tableName = "";
        String[] tagsNames = {};
        String[] tagsValue = {};
        boolean isStable = true; // 用于标识是否按超级表规则插入数据

        // 提取表名和标签
        Field[] superFields = superClass.getDeclaredFields();
        for (Field field : superFields) {
            field.setAccessible(true);
            String key = field.getName();
            Object value = field.get(object);
            if (!TDStatementTool.ifNull(value)) {
                if (key.equals(TDengineStatementContent.FIELD_NAME_TABLE_NAME)) {
                    tableName = value.toString();
                } else if (key.equals(TDengineStatementContent.FIELD_NAME_TAGS_NAMES)) {
                    tagsNames = (String[]) value;
                } else if (key.equals(TDengineStatementContent.FIELD_NAME_TAGS_VALUE)) {
                    tagsValue = (String[]) value;
                }
            }
        }

        /** 处理表名
         * 1.tableName为空则认为是顶级表 直接去当前类名作为表名
         * 2.默认: tableName不为空则认为是通过超级表插入数据 可自动创建表 即格式为 tableName USING 当前类名(即超级表名)
         * */
        String finalTableName = tableName + TDengineStatementContent.USING + TDStatementTool.getDatabase() + sTableName;
        if (TDStatementTool.isEmpty(tableName)) {
            finalTableName = sTableName;
            isStable = false;
        }
        sql.append(TDStatementTool.getDatabase()).append(tablePrefix).append(finalTableName);

        /**
         * 处理TAGS
         * 只有超级表的方式才可以设置
         */
        if (isStable) {
            StringBuffer tags = new StringBuffer();
            if (tagsNames.length > 0 && tagsNames.length == tagsValue.length) { // 有标签 有值

                tags.append(TDengineStatementContent.SPACE)
                        .append(TDengineStatementContent.OPEN_PAREN)
                        .append(StringUtils.join(tagsNames, TDengineStatementContent.COMMA))
                        .append(TDengineStatementContent.CLOSE_PAREN)
                        .append(TDengineStatementContent.TAGS)
                        .append(TDengineStatementContent.OPEN_PAREN)
                        .append(StringUtils.join(tagsValue, TDengineStatementContent.COMMA))
                        .append(TDengineStatementContent.CLOSE_PAREN);

            } else if (tagsNames.length == 0 && tagsValue.length > 0) { // 无标签 有值

                tags.append(TDengineStatementContent.TAGS)
                        .append(TDengineStatementContent.OPEN_PAREN)
                        .append(StringUtils.join(tagsValue, TDengineStatementContent.COMMA))
                        .append(TDengineStatementContent.CLOSE_PAREN);

            } else { // 剩下的全是错误的 抛出异常

                throw new TDengineStatementException(ExceptionMessage.TAGS_LENGTH_ERROR);
            }
            sql.append(tags);
        }
        return sql.toString();
    }


    private static String generateValues(Object object) throws IllegalAccessException {
        StringBuffer sql = new StringBuffer();
        Class c = object.getClass();
        Field[] fields = c.getDeclaredFields();
        /**
         * 每一个指端都要判断值是否为空 时序库的字段都不允许为空
         */
        if (!TDStatementTool.isEmpty(fields)) {
            sql.append(TDengineStatementContent.VALUES)
                    .append(TDengineStatementContent.OPEN_PAREN);
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                String key = field.getName();
                Object value = field.get(object);
                Class dataType = field.getType();
                if (TDStatementTool.ifNull(value)) {
                    throw new TDengineStatementException(key + ": " +ExceptionMessage.FIELD_VALUE_NOT_NULL);
                }

                if (dataType.equals(String.class)) {
                    value = String.format(TDengineStatementContent.APOS, value.toString());
                }
                sql.append(value).append(i != fields.length - 1 ? TDengineStatementContent.COMMA : TDengineStatementContent.EMPTY_STRING);
            }
            sql.append(TDengineStatementContent.CLOSE_PAREN);
        }
        return sql.toString();
    }

   @Override
    public String generateSelectStatement(Class c, TDengineFactor where) {
        String result = null;
        Field[] fields = c.getDeclaredFields();
        if (!TDStatementTool.isEmpty(fields)) {
            boolean isWhere = false; // 用于判断前面是否有条件

            StringBuffer sql = new StringBuffer();
            sql.append(TDengineStatementContent.SELECT + TDengineStatementContent.WRAP);
            // 字段处理
            if (!TDStatementTool.isEmpty(where.getAggregationType())) { // 聚合查询
                if (TDStatementTool.isEmpty(where.getAggregationAlias())) { // 聚合查询别名不能为空
                    throw new TDengineStatementException(ExceptionMessage.AGGREGATION_ALIAS_NOT_NULL);
                }
                for (int i = 0; i < where.getAggregationType().length; i++) {
                    String agt = where.getAggregationType()[i];
                    String aga = where.getAggregationAlias()[i];
                    String d = i < (where.getAggregationType().length - 1) ? TDengineStatementContent.COMMA :
                            TDengineStatementContent.EMPTY_STRING;
                    if (!TDStatementTool.isEmpty(agt) && !TDStatementTool.isEmpty(aga)) {
                        sql.append(TDengineStatementContent.SPACE)
                                .append(agt)
                                .append(TDengineStatementContent.AS)
                                .append(aga)
                                .append(d)
                                .append(TDengineStatementContent.WRAP);
                    }
                }

            } else { // 非聚合查询

                for (int i = 0; i < fields.length; i++) {
                    String fieldName = fields[i].getName();
                    String tempFieldName = i < (fields.length - 1) ? fieldName + TDengineStatementContent.COMMA : fieldName;
                    sql.append(TDengineStatementContent.SPACE).append(tempFieldName).append(TDengineStatementContent.WRAP);
                }
            }

            // 处理表名 如果实际的表名没有传 直接去当前当前类名 即超级表
            String tableName = TDStatementTool.isEmpty(where.getTableName()) ? TDStatementTool.camelToUnderline(c.getSimpleName(), false) : where.getTableName();
            String prefix = TDStatementTool.isEmpty(where.getTablePrefix()) ? "" : where.getTablePrefix();
//            if (CommonUtils.isEmpty(tableName)) { // 表名不能为空
//                throw new TDengineStatementException(ExceptionMessage.TABLE_NAME_NOT_NULL);
//            }
            sql.append(TDengineStatementContent.FROM)
                    .append(TDengineStatementContent.SPACE)
                    .append(TDStatementTool.getDatabase())
                    .append(prefix)
                    .append(tableName)
                    .append(TDengineStatementContent.WRAP);

            // 时间范围
            if (where.getStartTime() != null) {

                sql.append(TDengineStatementContent.WHERE)
                        .append(TDengineStatementContent.SPACE)
                        .append(TDengineStatementContent.TIMESTAMP_FIELD_NAME)
                        .append(TDengineStatementContent.GREATER)
                        .append(where.getStartTime())
                        .append(TDengineStatementContent.WRAP);
                isWhere = true;
            }
            if (where.getEndTime() != null) {
                sql.append(TDStatementTool.getWhereOrAnd(isWhere))
                        .append(TDengineStatementContent.TIMESTAMP_FIELD_NAME)
                        .append(TDengineStatementContent.LESS)
                        .append(where.getEndTime())
                        .append(TDengineStatementContent.WRAP);
                isWhere = true;
            }

            // 附加条件
            if (!TDStatementTool.isEmpty(where.getAppendWhere())) {
                sql.append(TDStatementTool.getWhereOrAnd(isWhere))
                        .append(StringUtils.join(where.getAppendWhere(), TDengineStatementContent.AND))
                        .append(TDengineStatementContent.WRAP);
            }

            // 处理统计周期
            if (!TDStatementTool.isEmpty(where.getInterval())) {
                sql.append(TDengineStatementContent.INTERVAL)
                        .append(TDengineStatementContent.OPEN_PAREN)
                        .append(StringUtils.join(where.getInterval(), TDengineStatementContent.COMMA))
                        .append(TDengineStatementContent.CLOSE_PAREN)
                        .append(TDengineStatementContent.WRAP);
            }

            // 处理group by
            if (!TDStatementTool.isEmpty(where.getGroupBy())) {
                sql.append(TDengineStatementContent.GROUP_BY)
                        .append(TDengineStatementContent.SPACE)
                        .append(StringUtils.join(where.getGroupBy(), TDengineStatementContent.COMMA))
                        .append(TDengineStatementContent.WRAP);
            }

            // 处理order by
            if (!TDStatementTool.isEmpty(where.getOrderBy())) {
                sql.append(TDengineStatementContent.ORDER_BY)
                        .append(TDengineStatementContent.SPACE)
                        .append(StringUtils.join(where.getOrderBy(), TDengineStatementContent.COMMA))
                        .append(TDengineStatementContent.SPACE)
                        .append(TDStatementTool.isEmpty(where.getOrderByType()) ? TDengineStatementContent.DESC : where.getOrderByType())
                        .append(TDengineStatementContent.WRAP);
            }

            // 处理分页
            if (where.getIndex() != null && where.getSize() == null) { // 有指针 无长度
                throw new TDengineStatementException(ExceptionMessage.LIMIT_SIZE_NOT_NULL);
            } else if (where.getIndex() != null && where.getSize() != null) { // 有指针 有长度
                sql.append(TDengineStatementContent.LIMIT)
                        .append(TDengineStatementContent.SPACE)
                        .append(where.getIndex())
                        .append(TDengineStatementContent.COMMA)
                        .append(where.getSize());
            } else if (where.getIndex() == null && where.getSize() != null) {
                sql.append(TDengineStatementContent.LIMIT)
                        .append(TDengineStatementContent.SPACE)
                        .append(where.getSize());
            }

            // 结尾
            sql.append(TDengineStatementContent.SEMICOLON);

            result = sql.toString();
        }
        return result;
    }


    @Override
    public Map<String, Object> generateSelectStatement(String sql, Parameter[] parameters, Object[] args) {
        Map<String, Object> result = new HashMap<>();
        String finalSql = sql;
        Map<Integer, Object> paramValueMap = new HashMap<>();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String param = String.format(TDengineStatementContent.PARAM_INDEX_OF, parameter.getName());
            int index = sql.indexOf(param); //当前参数的位置

            if (index == -1) { // 没找到参数 异常处理
                throw new TDataSourceException(String.format(ExceptionMessage.SQL_PARAMETER_NOT_FOUNT, param));
            }

            String replaceText = TDengineStatementContent.SQL_PARAM; //占位符

            if (args[i] instanceof Collection) { // 处理in参数的位置
                int tIn = 0; // In参数位置的累加
                StringBuffer stringBuffer = new StringBuffer();
                Collection inParams = (Collection) args[i];
                for (Object o : (Collection) args[i]) {
                    paramValueMap.put(index + tIn, o);
                    // 控制分隔符
                    String comma = tIn < inParams.size() - 1 ? TDengineStatementContent.COMMA : TDengineStatementContent.EMPTY_STRING;
                    // 插入对应数量的占位符
                    stringBuffer.append(TDengineStatementContent.SQL_PARAM).append(comma);

                    tIn++;

                }
                // 用展位符替换参数
                replaceText = stringBuffer.toString();

            } else { // 普通参数处理

                paramValueMap.put(index, args[i]);
            }

            finalSql = StringUtils.replace(finalSql, param, replaceText);
        }

        result.put(TDengineStatementContent.SQL_OBJECT_MAP_STATEMENT, finalSql);
        result.put(TDengineStatementContent.SQL_OBJECT_MAP_PARAMS, paramValueMap);

        return result;
    }


    @Override
    public String generateInsertStatement(Object object) {
        StringBuffer sql = new StringBuffer();

        try {
            if (!TDStatementTool.ifNull(object)) {
                String tableAndTags = generateTableAndTags(object);
                String values = generateValues(object);
                if (!TDStatementTool.isEmpty(tableAndTags) && !TDStatementTool.isEmpty(values)) {
                    sql.append(TDengineStatementContent.INSERT_INTO)
                            .append(TDengineStatementContent.SPACE)
                            .append(tableAndTags)
                            .append(values)
                            .append(TDengineStatementContent.SEMICOLON);
                }
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return sql.toString();
    }


    @Override
    public String generateInsertStatement(Collection<?> objects) {
        StringBuffer sql = new StringBuffer();
        if (!TDStatementTool.isEmpty(objects)) {
            StringBuffer targetSql = new StringBuffer();
            objects.forEach(object -> {
                try {
                    String tableAndTags = generateTableAndTags(object);
                    String values = generateValues(object);
                    if (!TDStatementTool.isEmpty(tableAndTags) && !TDStatementTool.isEmpty(values)) {
                        targetSql.append(tableAndTags).append(values).append(TDengineStatementContent.WRAP);
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });

            if (!TDStatementTool.isEmpty(targetSql.toString())) {
                sql.append(TDengineStatementContent.INSERT_INTO)
                        .append(TDengineStatementContent.SPACE)
                        .append(targetSql)
                        .append(TDengineStatementContent.SEMICOLON);
            }
        }
        return sql.toString();
    }
}
