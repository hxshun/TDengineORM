package io.shun.tdengine.constants;

/**
 * @ClassName: ExceptionMessage
 * @Description:
 * @Author: ShunZ
 * @CreatedAt: 2021-12-01 11:57
 * @Version: 1.0
 **/

public class ExceptionMessage {
    private static final String PREFIX = "TDengine: ";
    public static final String DRIVER_NOT_FOUNT = PREFIX + "driver not fount";
    public static final String USERNAME_NOT_NULL = PREFIX + "username not null";
    public static final String PASSWORD_NOT_NULL = PREFIX + "password not null";
    public static final String TABLE_NAME_NOT_NULL = PREFIX + "table namenot null";
    public static final String AGGREGATION_ALIAS_NOT_NULL = PREFIX + "aggregation alias not null";
    public static final String LIMIT_SIZE_NOT_NULL = PREFIX + "limit size not null";
    public static final String FIELD_VALUE_NOT_NULL = PREFIX + "field value not null";
    public static final String TAGS_LENGTH_ERROR = PREFIX + "tags length error";
    public static final String ANNOTATION_TDENGINESELECT_NOT_FOUNT = PREFIX + "Annotation TDengineSelect Not Fount";
    public static final String SQL_PARAMETER_NOT_FOUNT = PREFIX + "SQL Parameter Not Fount To %s";
}
