package io.shun.tdengine.aspect;

import io.shun.tdengine.constants.TDengineStatementContent;
import io.shun.tdengine.entity.TDataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * Created by shunZ on 2021/12/27 下午1:48
 */
@Aspect
@Slf4j
@Component
public class TDengineAspect {

    @Autowired
    TDataSourceProperties properties;

    /**
     * 打印sql日志 根据数据配置补全数据前缀
     * @param joinPoint
     */
    @AfterReturning( value = "execution(* io.shun.tdengine.processor.TDengineGenerateStatement.*(..))", returning = "object")
    public void doAfterReturningTask(JoinPoint joinPoint, Object object) {
        try{
            // 根据配置打印sql语句
            Boolean printSql = properties.getPrintSql();
            if (printSql != null && printSql) {
                String sql = "";
                if (object instanceof Map) {
                    sql = ((Map) object).get(TDengineStatementContent.SQL_OBJECT_MAP_STATEMENT).toString();
                } else {
                    sql = object.toString();
                }

                log.info("\n TDengine Generate SQL Statement : " + sql);
            }

        } catch (Throwable e) {
            log.error("TDengineAspectError: {}",  e.getMessage());
        }
    }
}
