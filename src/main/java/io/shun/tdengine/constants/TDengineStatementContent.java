package io.shun.tdengine.constants;

/**
 * @ClassName: Statement
 * @Description: Sql语句相关
 * @Author: ShunZ
 * @CreatedAt: 2021-12-03 11:36
 * @Version: 1.0
 **/

public class TDengineStatementContent {
    public static final String SELECT = "SELECT";
    public static final String INSERT_INTO = "INSERT INTO";
    public static final String FROM = "FROM";
    public static final String WHERE = "WHERE";
    public static final String AS = " AS ";
    public static final String AND = " AND ";
    public static final String INTERVAL = "INTERVAL";
    public static final String GROUP_BY = "GROUP BY";
    public static final String ORDER_BY = "ORDER BY";
    public static final String LIMIT = "LIMIT";
    public static final String DESC = "DESC";
    public static final String ASC = "ASC";
    public static final String WRAP = "\n";
    public static final String SPACE = " ";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String GREATER = " >= ";
    public static final String LESS = " <= ";
    public static final String OPEN_PAREN = "(";
    public static final String CLOSE_PAREN = ")";
    public static final String EMPTY_STRING = "";
    public static final char UNDERLINE = '_';
    public static final String USING = " USING ";
    public static final String TAGS = " TAGS ";
    public static final String VALUES = " VALUES ";
    public static final String APOS = "\'%s\'";
    public static final String PERIOD = ".";
    public static final String PARAM_INDEX_OF = "#{%s}";
    public static final String SQL_PARAM = "?";
    public static final String SQL_OBJECT_MAP_STATEMENT= "sql";
    public static final String SQL_OBJECT_MAP_PARAMS = "params";

    public static final String TIMESTAMP_FIELD_NAME = "ts";
    public static final String FIELD_NAME_TABLE_NAME = "tableName";
    public static final String FIELD_NAME_TABLE_PREFIX = "tablePrefix";
    public static final String FIELD_NAME_TAGS_NAMES = "tagsNames";
    public static final String FIELD_NAME_TAGS_VALUE = "tagsValue";


    public static final String DEFAULT_RESOURCE_PATTERN = "/**/*.class";
}
