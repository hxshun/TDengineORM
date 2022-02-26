package io.shun.tdengine.processor;


import io.shun.tdengine.constants.TDengineStatementContent;
import io.shun.tdengine.entity.TDataSourceProperties;
import io.shun.tdengine.utils.TDengineSpringContextUtil;

import java.util.Collection;


/**
 * @ClassName: CommonUtils
 * @Description: sql语句实现所需的核心工具类 类和方法均为包访问权限 无法外部调用
 * @Author: ShunZ
 * @CreatedAt: 2021-12-03 16:07
 * @Version: 1.0
 **/

 class TDStatementTool {
     static boolean ifNull(Object object) {
        return object == null;
    }

    /**
     * 字符串为空
     * @param s
     * @return
     */
     static boolean isEmpty(String s) {
        if (null == s || "".equals(s) || "".equals(s.trim()) || "null".equalsIgnoreCase(s)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 数组为空
     * @param o
     * @return
     */
     static boolean isEmpty(Object[] o) {
        if (null == o || o.length == 0) {
            return true;
        } else {
            return false;
        }
    }


     static boolean isEmpty(Collection<?> coll) {
        return ifNull(coll) || coll.isEmpty();
    }

     static String getWhereOrAnd(boolean isWhere) {
        return isWhere ? " AND " : "WHERE ";
    }

    /**
     * 将下划线转为大驼峰
     * @param str   目标字符串
     * @return  变为大驼峰的字符串
     */
     static String underlineToBigCamel(String str){
        return underlineToSmallCamel(str.toUpperCase().substring(0,1)+str.substring(1));
    }

    /**
     * 将下划线转为小驼峰
     * @param str   目标字符串
     * @return  变为小驼峰的字符串
     */
     static String underlineToSmallCamel(String str){
        if (str==null||"".equals(str.trim())){
            return "";
        }
        int len=str.length();
        StringBuilder sb=new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c=str.charAt(i);
            if (c=='_'){
                if (++i<len){
                    sb.append(Character.toUpperCase(str.charAt(i)));
                }
            }else{
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰转下划线
     * @param param
     * @param charType
     * @return
     */
     static String camelToUnderline(String param, boolean charType) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append(TDengineStatementContent.UNDERLINE);
            }
            if (charType) {
                sb.append(Character.toUpperCase(c));  //统一都转大写
            } else {
                sb.append(Character.toLowerCase(c));  //统一都转小写
            }


        }
        return sb.toString();
    }


    /**
     * 获取数据库配置
     * @return
     */
    static String getDatabase() {
        TDataSourceProperties tDataSourceProperties = TDengineSpringContextUtil.getBean(TDataSourceProperties.class);
        String database =tDataSourceProperties.getDatabase();
        return TDStatementTool.isEmpty(database) ? TDengineStatementContent.EMPTY_STRING : database + TDengineStatementContent.PERIOD;
    }
}
